package ca.hullabaloo.properties;

import net.sf.cglib.core.Converter;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static ca.hullabaloo.properties.Utils.checkArgument;

class CglibPropertyImpl<T> {
    private static final Map<Class<?>, Converter> CONVERTERS;

    static {
        Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>(4);
        converters.put(String.class, new Converter() {
            public Object convert(Object value, Class target, Object context) {
                return value == null ? null : value.toString();
            }
        });
        converters.put(Integer.class, new Converter() {
            public Object convert(Object value, Class target, Object context) {
                if (value == null)
                    return null;
                if (value instanceof String)
                    return Integer.parseInt((String) value);
                if (value instanceof Number)
                    return ((Number) value).intValue();
                throw new ClassCastException("could not convert to Integer " + value.getClass());
            }
        });
        converters.put(Integer.TYPE, new Converter() {
            public Object convert(Object value, Class target, Object context) {
                if (value == null)
                    throw new NullPointerException();
                if (value instanceof String)
                    return Integer.parseInt((String) value);
                if (value instanceof Number)
                    return ((Number) value).intValue();
                throw new ClassCastException("could not convert to Integer " + value.getClass());
            }
        });

        CONVERTERS = converters;
    }

    public static <T> T create(Resolver props, Class<T> type) {
        validateType(type);
        return new CglibPropertyImpl<T>(props, type).createInstance();
    }

    private static <T> void validateType(Class<T> type) {
        if (!Modifier.isPublic(type.getModifiers()) || Modifier.isFinal(type.getModifiers()))
            throw JavaPropertiesException.nonPublicOrFinalClass(type);
        if (type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers()))
            throw JavaPropertiesException.nonStaticInnerClass(type);
    }

    private final Resolver props;

    private final Class<T> type;

    private CglibPropertyImpl(Resolver props, Class<T> type) {
        this.props = props;
        this.type = type;
    }

    private T createInstance() {
        JavaPropertiesException.Builder errors = JavaPropertiesException.builder();

        // see potentialMethods()
        checkArgument(type.getSuperclass() == Object.class || type.isInterface());
        boolean constants = Constants.class.isAssignableFrom(this.type);

        List<Callback> callbacks = new ArrayList<Callback>();
        callbacks.add(NoOp.INSTANCE);
        Map<Method, Integer> callbackIndex = new HashMap<Method, Integer>();
        for (Method m : potentialMethods()) {
            boolean constantValue = false;
            checkArgument(m.getParameterTypes().length == 0);

            // potentialMethods() filters out things we don't have converters for,
            // so 'converter' will never be null
            Class valueType = m.getReturnType();
            Converter converter = CONVERTERS.get(valueType);

            String prop = Utils.propertyName(m);
            Object value = this.props.resolve(prop);
            if (value == null) {
                value = fromDefaultAnnotation(m);
                constantValue = true;
            }
            if (value != null) {
                try {
                    value = converter.convert(value, valueType, null);
                } catch (RuntimeException e) {
                    //noinspection ThrowableResultOfMethodCallIgnored
                    errors.conversionError(m, value, e);
                }
            }
            if (value == null) {
                // nothing; we'll check if this is OK later
            } else if (constants || constantValue) {
                callbackIndex.put(m, callbacks.size());
                callbacks.add(new FixedValueImpl(value));
            } else {
                callbackIndex.put(m, callbacks.size());
                callbacks.add(new LiveValueImpl(valueType, converter, prop, props));
            }
        }

        Enhancer e = new Enhancer();
        e.setUseFactory(false);
        e.setUseCache(false);
        e.setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
        e.setCallbackFilter(new MapCallbackFilter(callbackIndex));

        if (this.type.isInterface()) {
            e.setInterfaces(new Class[]{this.type});
        } else {
            e.setSuperclass(this.type);
        }

        Object instance = e.create();
        validateConstructedType(instance.getClass(), errors);
        errors.throwIfErrors(this.type);

        return castToType(instance);
    }

    private Object fromDefaultAnnotation(Method m) {
        Default def = m.getAnnotation(Default.class);
        return def == null ? null : def.value();
    }

    private Iterable<Method> potentialMethods() {
        // since we don't support inheritance for our types, this can simply
        // be current class + interface methods.   Note that if a class implements
        // an interface method, IT is the declaring class, but if it does not
        // (say, it's an abstract class) than the declaring class is still
        // the interface
        Set<Method> result = new LinkedHashSet<Method>();
        for (Method m : this.type.getDeclaredMethods()) {
            if (Modifier.isFinal(m.getModifiers()) || Modifier.isPrivate(m.getModifiers()))
                continue;
            if (isSupportedReturnType(m.getReturnType()))
                result.add(m);
        }
        for (Method m : this.type.getMethods()) {
            if (m.getDeclaringClass().isInterface())
                if (isSupportedReturnType(m.getReturnType()))
                    result.add(m);
        }
        return result;
    }

    private boolean isSupportedReturnType(Class<?> returnType) {
        return CONVERTERS.keySet().contains(returnType);
    }

    @SuppressWarnings({"unchecked"})
    private T castToType(Object o) {
        return (T) o;
    }

    private void validateConstructedType(Class constructed, JavaPropertiesException.Builder errors) {
        for (Method m : constructed.getMethods()) {
            if (Modifier.isAbstract(m.getModifiers()))
                addError(errors, m);
        }
        for (Method m : constructed.getDeclaredMethods()) {
            if (Modifier.isAbstract(m.getModifiers()))
                addError(errors, m);
        }
    }

    private void addError(JavaPropertiesException.Builder errors, Method m) {
        boolean added = false;
        if (!isSupportedReturnType(m.getReturnType())) {
            errors.unsupportedReturnType(m);
            added = true;
        }
        if (this.props.resolve(Utils.propertyName(m)) == null) {
            errors.missingProperty(Utils.propertyName(m), m);
            added = true;
        }
        if (!added) {
            errors.unknownError(m);
        }
    }

    private static class FixedValueImpl implements FixedValue {
        private final Object value;

        public FixedValueImpl(Object value) {
            this.value = value;
        }

        public Object loadObject() throws Exception {
            return this.value;
        }
    }

    private static class MapCallbackFilter implements CallbackFilter {
        private final Map<Method, Integer> callbackIndexes;

        public MapCallbackFilter(Map<Method, Integer> callbackIndexes) {
            this.callbackIndexes = callbackIndexes;
        }

        public int accept(Method method) {
            Integer i = this.callbackIndexes.get(method);
            return i == null ? 0 : i;
        }
    }

    private static class LiveValueImpl implements MethodInterceptor {
        private final Class valueType;
        private final Converter converter;
        private final String name;
        private final Resolver props;

        public LiveValueImpl(Class valueType, Converter converter, String name, Resolver props) {
            this.valueType = valueType;
            this.converter = converter;
            this.name = name;
            this.props = props;
        }

        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
            Object value = props.resolve(name);
            return converter.convert(value, valueType, null);
        }
    }
}
