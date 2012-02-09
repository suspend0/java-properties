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
  public <T> T convert(String s, Class<T> targetType) {
    checkArgument(supportsTarget(targetType));
    Exception cause = null;
    try {
      Class<? extends Enum> enumType = targetType.asSubclass(Enum.class);
      return (T) Enum.valueOf(enumType, s);
    } catch (IllegalArgumentException e) {
      // from enum.valueOf
      cause = e;
    } catch (ArrayIndexOutOfBoundsException e) {
      // from getEnumConstants
      cause = e;
    }
    ClassCastException e = new ClassCastException("Cannot translate '" + s + "' into " + targetType);
    if (cause != null) {
      e.initCause(cause);
    }
    throw e;
  }
}
