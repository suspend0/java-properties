package ca.hullabaloo.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Converters are used to translate property data (usually a String) into the declared
 * return type of the method that's bound to that property value.
 * <p/>
 * This set of converters is for the basic types String,Integer,Long,Float,Double
 * and the primitive versions of same.
 */
class StandardConverters {
  private static final Converter ALL;

  static {
    final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>(4);
    converters.put(String.class, new BaseConverter<String>(String.class) {
      public String convert(String value) {
        return value;
      }
    });
    converters.put(Integer.class, new BaseConverter<Integer>(Integer.class) {
      public Integer convert(String value) {
        return Integer.parseInt(value);
      }
    });
    converters.put(Integer.TYPE, new BaseConverter<Integer>(Integer.class) {
      public Integer convert(String value) {
        return Integer.parseInt(value);
      }
    });
    converters.put(Double.class, new BaseConverter<Double>(Double.class) {
      public Double convert(String value) {
        return Double.parseDouble(value);
      }
    });
    converters.put(Double.TYPE, new BaseConverter<Double>(Double.class) {
      public Double convert(String value) {
        return Double.parseDouble(value);
      }
    });
    converters.put(Long.class, new BaseConverter<Long>(Long.class) {
      public Long convert(String value) {
        return Long.parseLong(value);
      }
    });
    converters.put(Long.TYPE, new BaseConverter<Long>(Long.class) {
      public Long convert(String value) {
        return Long.parseLong(value);
      }
    });
    converters.put(Float.class, new BaseConverter<Float>(Float.class) {
      public Float convert(String value) {
        return Float.parseFloat(value);
      }
    });
    converters.put(Float.TYPE, new BaseConverter<Float>(Float.class) {
      public Float convert(String value) {
        return Float.parseFloat(value);
      }
    });

    ALL = new Converter() {
      public boolean supportsTarget(Class<?> type) {
        return converters.containsKey(type);
      }

      public <T> T convert(String s, Class<T> targetType) {
        return converters.get(targetType).convert(s, targetType);
      }
    };
  }

  public static Converter all() {
    return ALL;
  }
}
