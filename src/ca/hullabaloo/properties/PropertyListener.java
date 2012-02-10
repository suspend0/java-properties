package ca.hullabaloo.properties;

public interface PropertyListener<T> {
  void fire(PropertyValue<T> newValue, T oldValue);
}
