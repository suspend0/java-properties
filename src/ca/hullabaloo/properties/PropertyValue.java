package ca.hullabaloo.properties;

public interface PropertyValue<T> {
  String name();
  T get();
  void set(T newValue);
  void listen(PropertyListener<T> listener);
}
