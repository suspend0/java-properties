package ca.hullabaloo.properties;

/**
 * Utility methods to work on {@link Converter} instances
 */
public class Converters {
    /**
     * Returns a converter that converts the "standard java types": String, int, long, float, double
     */
    public static Converter standard() {
        return StandardConverters.all();
    }

    public static Converter enums() {
        return EnumConverter.INSTANCE;
    }

    /**
     * Adapts a single converter so that arrays of the same supported types
     * can be converted.  Requires inbound strings of the same format
     * as {@link java.util.Arrays#toString}
     */
    public static Converter forArray(Converter singleTypeConverter) {
        return ArrayConverter.wrap(singleTypeConverter);
    }

    /**
     * Chains multiple converters in sequence.  A call to a method on the
     * {@link Converter} interface will call each of the provided converters
     * in order until {@link Converter#supportsTarget(Class)} return true.
     */
    public static Converter combine(Converter... converters) {
        final Converter[] array = converters.clone();
        return new Converter() {
            public boolean supportsTarget(Class<?> type) {
                for (Converter converter : array) {
                    if (converter.supportsTarget(type))
                        return true;
                }
                return false;
            }

            public <T> T convert(Object object, Class<T> targetType) {
                for (Converter converter : array) {
                    if (converter.supportsTarget(targetType))
                        return converter.convert(object, targetType);
                }
                throw new ClassCastException("could not convert");
            }
        };
    }
}
