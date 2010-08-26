package ca.hullabaloo.properties;

public class Converters {
    public static Converter combine(final Converter... converters) {
        return new Converter() {
            public boolean supportsType(Class<?> type) {
                for (Converter converter : converters) {
                    if (converter.supportsType(type))
                        return true;
                }
                return false;
            }

            public <T> T convert(Object object, Class<T> targetType) {
                for (Converter converter : converters) {
                    if (converter.supportsType(targetType))
                        return converter.convert(object, targetType);
                }
                throw new ClassCastException("could not convert");
            }
        };
    }
}
