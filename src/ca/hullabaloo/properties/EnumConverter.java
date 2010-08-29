package ca.hullabaloo.properties;

import static ca.hullabaloo.properties.Utils.checkArgument;

public class EnumConverter implements Converter {
    public boolean supportsType(Class<?> type) {
        return type.isEnum();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T convert(Object object, Class<T> targetType) {
        checkArgument(supportsType(targetType));
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

    public static Converter instance() {
        return new EnumConverter();
    }
}
