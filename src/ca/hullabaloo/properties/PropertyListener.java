package ca.hullabaloo.properties;

/**
 * A callback interface that gets notified when a property is changed
 */
public interface PropertyListener<T> {
  void fire(String name, T newValue, T oldValue);
}
