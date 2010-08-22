package ca.hullabaloo.properties;

import java.lang.reflect.Method;

public class JavaPropertyException extends IllegalArgumentException {
    static JavaPropertyException nonPublicAbstractMethod(Method method) {
        return create("Only public abstract methods are supported %s", method);
    }

    static JavaPropertyException nonPublicOrFinalClass(Class<?> type) {
        return create("Only public non-final classes are supported ", type);
    }

    public static JavaPropertyException nonStaticInnerClass(Class<?> type) {
        return create("Non-static inner classes are not supported ", type);
    }

    public static JavaPropertyException missingProperty(String propertyName, Method propertyMethod) {
        return create("Missing property '%s' to implement %s", propertyName, propertyMethod);
    }

    private static JavaPropertyException create(String format, Object... args) {
        return new JavaPropertyException(format, args);
    }

    private final String format;

    private final Object[] args;

    protected JavaPropertyException(String format, Object... args) {
        this.format = format;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return String.format(this.format, this.args);
    }
}
