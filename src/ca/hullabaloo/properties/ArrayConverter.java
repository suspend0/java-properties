package ca.hullabaloo.properties;

import java.lang.reflect.Array;

import static ca.hullabaloo.properties.Utils.checkArgument;

class ArrayConverter implements Converter {
    public static ArrayConverter create(Converter componentConverter) {
        return new ArrayConverter(componentConverter);
    }

    private final Converter componentConverter;

    private ArrayConverter(Converter componentConverter) {
        this.componentConverter = componentConverter;
    }

    public boolean supportsType(Class<?> type) {
        return type.isArray() && componentConverter.supportsType(type.getComponentType());
    }

    public <T> T convert(Object object, Class<T> arrayType) {
        checkArgument(supportsType(arrayType));
        if (arrayType.isInstance(object))
            return arrayType.cast(object);
        if (object instanceof String)
            return parse((String) object, arrayType);
        throw new ClassCastException("could not convert");
    }

    /**
     * Parses a string of form "[a,b]" into an array of the provided type
     */
    private <T> T parse(String str, Class<T> arrayType) {
        Class<?> componentType = arrayType.getComponentType();
        str = str.trim();
        if (str.length() > 1 && str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']') {
            str = str.substring(1, str.length() - 1);
            String[] parts = str.split(",");
            Object result = Array.newInstance(componentType, parts.length);
            for (int i = 0; i < parts.length; i++) {
                Object value = componentConverter.convert(parts[i].trim(), componentType);
                Array.set(result, i, value);
            }
            return arrayType.cast(result);
        }
        throw new IllegalArgumentException("Expected array format [a,b] " + str);
    }
}
