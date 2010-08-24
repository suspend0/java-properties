package ca.hullabaloo.properties;

import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static ca.hullabaloo.properties.Utils.checkArgument;

class CglibPropertyImpl<T> {
    private static final Set<Class<?>> SUPPORTED_RETURN_TYPES = new HashSet<Class<?>>(Arrays.<Class<?>>asList(
            String.class));

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
        // see potentialMethods()
        checkArgument(type.getSuperclass() == Object.class || type.isInterface());
        boolean constants = Constants.class.isAssignableFrom(this.type);

        List<Callback> callbacks = new ArrayList<Callback>();
        callbacks.add(NoOp.INSTANCE);
        Map<Method, Integer> callbackIndex = new HashMap<Method, Integer>();
        for (Method m : potentialMethods()) {
            boolean constantValue = false;
            checkArgument(m.getParameterTypes().length == 0);

            String prop = Utils.propertyName(m);
            Object value = this.props.resolve(prop);
            if (value == null) {
                value = fromDefaultAnnotation(m);
                constantValue = true;
            }
            if (value == null) {
                // nothing; we'll check if this is OK later
            } else if (constants || constantValue) {
                callbackIndex.put(m, callbacks.size());
                callbacks.add(new FixedValueImpl(value));
            } else {
                callbackIndex.put(m, callbacks.size());
                callbacks.add(new LiveValueImpl(prop, props));
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
        validateConstructedType(instance.getClass());
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
        return SUPPORTED_RETURN_TYPES.contains(returnType);
    }

    @SuppressWarnings({"unchecked"})
    private T castToType(Object o) {
        return (T) o;
    }

    private void validateConstructedType(Class constructed) {
        JavaPropertiesException e = null;
        for (Method m : constructed.getMethods()) {
            if (Modifier.isAbstract(m.getModifiers()))
                e = addError(e, m);
        }
        for (Method m : constructed.getDeclaredMethods()) {
            if (Modifier.isAbstract(m.getModifiers()))
                e = addError(e, m);
        }
        if (e != null) {
            throw e;
        }
    }

    private JavaPropertiesException addError(JavaPropertiesException e, Method m) {
        if (e == null) {
            e = new JavaPropertiesException("Some methods could not be resolved on type %s", this.type);
        }

        boolean added = false;
        if (!isSupportedReturnType(m.getReturnType())) {
            e.unsupportedReturnType(m);
            added = true;
        }
        if (this.props.resolve(Utils.propertyName(m)) == null) {
            e.missingProperty(Utils.propertyName(m), m);
            added = true;
        }
        if (!added) {
            e.unknownError(m);
        }
        return e;
    }

    private class FixedValueImpl implements FixedValue {
        private final Object value;

        public FixedValueImpl(Object value) {
            this.value = value;
        }

        public Object loadObject() throws Exception {
            return this.value;
        }
    }

    private class MapCallbackFilter implements CallbackFilter {
        private final Map<Method, Integer> callbackIndexes;

        public MapCallbackFilter(Map<Method, Integer> callbackIndexes) {
            this.callbackIndexes = callbackIndexes;
        }

        public int accept(Method method) {
            Integer i = this.callbackIndexes.get(method);
            return i == null ? 0 : i;
        }
    }

    private class LiveValueImpl implements MethodInterceptor {
        private final String name;
        private final Resolver props;

        public LiveValueImpl(String name, Resolver props) {
            this.name = name;
            this.props = props;
        }

        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return props.resolve(name);
        }
    }
}
