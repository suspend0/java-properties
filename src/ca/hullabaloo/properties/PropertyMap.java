package ca.hullabaloo.properties;

/**
 * A map of property names to objects that has efficient repeated reading of the current value (just a volatile read)
 * and permits callers to listen for property changes {@link #listen(PropertyListener) globally on the map}
 * or {@link PropertyValue#listen(PropertyListener) on a particular value}.
 *
 * This map does not permit null keys or values.
 */
public interface PropertyMap {
  public <T> PropertyValue<T> obtain(String key, T defaultValue);
  public void listen(PropertyListener<?> listener);
}

