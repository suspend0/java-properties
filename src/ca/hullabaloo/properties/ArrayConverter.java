package ca.hullabaloo.properties;

import java.lang.reflect.Array;

import static ca.hullabaloo.properties.Utils.checkArgument;
import static ca.hullabaloo.properties.Utils.q;

/**
 * Wraps a regular {@Converter} to parse arrays of same from strings formatted the way
 * {@link java.util.Arrays#toString} writes them: [a,b]
 *
 * @see #wrap(Converter)
 */
class ArrayConverter implements Converter {
  public static ArrayConverter wrap(Converter componentConverter) {
    return new ArrayConverter(componentConverter);
  }

  private final Converter componentConverter;

  private ArrayConverter(Converter componentConverter) {
    if (componentConverter == null) {
      throw new NullPointerException();
    }
    this.componentConverter = componentConverter;
  }

  @Override public boolean supportsTarget(Class<?> type) {
    return type.isArray() && componentConverter.supportsTarget(type.getComponentType());
  }

  @Override public <T> T convert(String s, Class<T> arrayType) {
    checkArgument(supportsTarget(arrayType));
    return s == null ? null : parse(s, arrayType);
  }

  /**
   * Parses a string of form "[a,b]" into an array of the provided type
   */
  private <T> T parse(String str, Class<T> arrayType) {
    Class<?> componentType = arrayType.getComponentType();
    str = str.trim();
    if (str.length() > 1 && str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']') {
      str = str.substring(1, str.length() - 1);
      String COMMA = ",";
      String[] parts = str.split(COMMA);
      int partCount = parts.length;

      // get rid of any whitespace
      for (int i = 0; i < parts.length; i++) {
        parts[i] = parts[i].trim();
      }

      // reassemble strings split on backslash-escaped commas
      for (int i = 0, j = 1; j < parts.length; j++) {
        if (parts[i].endsWith("\\")) {
          parts[i] = parts[i].substring(0, parts[i].length() - 1);
          parts[i] += COMMA + parts[j];
          parts[j] = null;
          --partCount;
        } else {
          i = j;
        }
      }

      // Convert each component piece
      Object result = Array.newInstance(componentType, partCount);
      for (int i = 0, j = 0; i < parts.length; i++) {
        if (parts[i] != null) {
          Object value = componentConverter.convert(parts[i], componentType);
          Array.set(result, j++, value);
        }
      }
      return arrayType.cast(result);
    }
    throw new IllegalArgumentException("Expected array format like [a,b] got " + q(str));
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ArrayConverter) {
      ArrayConverter that = (ArrayConverter) o;
      return this.componentConverter.equals(that.componentConverter);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 1867 * componentConverter.hashCode();
  }
}
