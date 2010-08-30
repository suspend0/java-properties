package ca.hullabaloo.properties;

import static ca.hullabaloo.properties.Utils.checkArgument;

/**
 * Converts an enum type from its string name to the enum value
 */
enum EnumConverter implements Converter {
    INSTANCE;

    public boolean supportsTarget(Class<?> type) {
        return type.isEnum();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T convert(Object object, Class<T> targetType) {
        checkArgument(supportsTarget(targetType));
        Exception cause = null;
        try {
            Class<? extends Enum> enumType = targetType.asSubclass(Enum.class);
            if (object instanceof String)
                return (T) Enum.valueOf(enumType, (String) object);
            if (object instanceof Number)
                return (T) enumType.getEnumConstants()[((Number) object).intValue()];
        } catch (IllegalArgumentException e) {
            // from enum.valueOf
            cause = e;
        } catch (ArrayIndexOutOfBoundsException e) {
            // from getEnumConstants
            cause = e;
        }
        ClassCastException e = new ClassCastException("Cannot translate '" + object + "' into " + targetType);
        if (cause != null) e.initCause(cause);
        throw e;
    }
}
