package ca.hullabaloo.properties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class InterfaceProperties {
    public static <T> T create(Resolver props, Class<T> type) {
        validate(props, type);
        return createProxy(type, new InterfaceMethodHandler(props));
    }

    private static <T> void validate(Resolver props, Class<T> type) {
        Utils.checkArgument(type.isInterface());
        for (Method m : type.getMethods())
            Utils.checkArgument(props.resolve(Utils.rootOf(m.getName())) != null
                    || m.isAnnotationPresent(Default.class));
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
            String value = props.resolve(name);
            if (value == null)
                value = method.getAnnotation(Default.class).value();
            return value;
        }
    }
}
