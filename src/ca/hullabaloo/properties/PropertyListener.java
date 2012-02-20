package ca.hullabaloo.properties;

/**
 * A callback interface that gets notified when a property is changed
 */
public interface PropertyListener<T> {
  void hear(String name, T newValue, T oldValue);
}
