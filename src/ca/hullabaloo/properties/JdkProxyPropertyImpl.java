package ca.hullabaloo.properties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

class JdkProxyPropertyImpl {
    public static <T> T create(Resolver props, Class<T> type) {
        Utils.checkArgument(type.isInterface());
        Map<Method, Object> values = validate(props, type);
        InvocationHandler h = Constants.class.isAssignableFrom(type)
                ? new ConstantMethodHandler(values)
                : new InterfaceMethodHandler(props);
        return createProxy(type, h);
    }

    private static <T> Map<Method, Object> validate(Resolver props, Class<T> type) {
        Map<Method,Object> result = new HashMap<Method, Object>();
        for (Method m : type.getDeclaredMethods()) {
            String name = Utils.propertyName(m);
            Object value = props.resolve(name);
            if (value == null)
                value = defaultOf(m);
            if (value == null)
                throw JavaPropertyException.missingProperty(name, m);
            result.put(m,value);
        }
        return result;
    }

    private static Object defaultOf(Method m) {
        Default d = m.getAnnotation(Default.class);
        return d == null ? null : d.value();
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T createProxy(Class<T> type, InvocationHandler h) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type},
                h);
    }

    private static class InterfaceMethodHandler implements InvocationHandler {
        Resolver props;

        public InterfaceMethodHandler(Resolver props) {
            this.props = props;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = Utils.rootOf(method.getName());
            Object value = props.resolve(name);
            if (value == null)
                value = method.getAnnotation(Default.class).value();
            return value;
        }
    }

    private static class ConstantMethodHandler implements InvocationHandler {
        private final Map<Method, Object> values;

        public ConstantMethodHandler(Map<Method, Object> values) {
            this.values = values;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return values.get(method);
        }
    }
}
