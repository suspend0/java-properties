package ca.hullabaloo.properties;

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
  public final <X> X convert(String s, Class<X> targetType) {
    // 'int' converters get passed targetType is Integer.
    // checkArgument(supportsTarget(targetType));
    if (s == null) {
      if (targetType.isPrimitive()) {
        throw new NullPointerException("Cannot convert 'null' to a " + targetType);
      }
      return null;
    }
    return (X) convert(s);
  }

  /**
   * @param s will not be null *
   */
  protected abstract T convert(String s);
}
