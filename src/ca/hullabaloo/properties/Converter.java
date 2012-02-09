package ca.hullabaloo.properties;

/**
 * Instances of this class should support converting from String to
 * the target type.  Supporting additional inbound types is nice but
 * not strictly required.  (If this converts String to Integer, it
 * should convert Number to Integer too.)
 */
interface Converter {
  /**
   * Return true if this type can be passed to {@link #convert} targetType
   */
  public boolean supportsTarget(Class<?> targetType);

  /**
   * Convert the inbound object to the target type
   *
   * @param s          to be converted
   * @param targetType the result type {@link #supportsTarget(Class)}
   * @return the converted type
   * @throws IllegalArgumentException if this is an invalid targetType
   * @throws ClassCastException       if the inbound type cannot be converted
   */
  public <T> T convert(String s, Class<T> targetType);
}
