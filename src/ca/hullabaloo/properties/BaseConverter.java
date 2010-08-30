package ca.hullabaloo.properties;

import static ca.hullabaloo.properties.Utils.checkArgument;

/**
 * Base class for simple type conversion.  It compensates for the somewhat strange
 * generic signature of Converter.
 */
abstract class BaseConverter<T> implements Converter {
    private final Class<T> targetType;

    protected BaseConverter(Class<T> targetType) {
        this.targetType = targetType;
    }

    public final boolean supportsTarget(Class<?> type) {
        return type == targetType;
    }

    /**
     * X must be a subtype of T
     */
    @SuppressWarnings({"unchecked"})
    public final <X> X convert(Object object, Class<X> targetType) {
        // 'int' converters get passed targetType is Integer.
        // checkArgument(supportsTarget(targetType));
        return (X) convert(object);
    }

    protected abstract T convert(Object object);
}
