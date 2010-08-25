package ca.hullabaloo.properties;

public interface Converter {
    public boolean supportsType(Class<?> type);

    public <T> T convert(Object object, Class<T> targetType);
}
