package ca.hullabaloo.properties;

public interface PropertyMap {
  public <T> PropertyValue<T> obtain(String key, T defaultValue);
  public void listen(PropertyListener<?> listener);
}

