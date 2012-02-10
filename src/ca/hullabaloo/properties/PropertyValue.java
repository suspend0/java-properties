package ca.hullabaloo.properties;

/**
 * A mutable property value supporting efficient repeated reads.
 */
public interface PropertyValue<T> {
  T get();

  void set(T newValue);

  void listen(PropertyListener<? super T> listener);
}
