package ca.hullabaloo.properties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyMapImpl implements PropertyMap {
  @SuppressWarnings("unchecked")
  private static <T> PropertyValue<T> cast(Val v) {
    return (PropertyValue<T>) v;
  }

  private CopyOnWriteArrayList<PropertyListener<?>> listeners = new CopyOnWriteArrayList<PropertyListener<?>>();
  private ConcurrentMap<String, Val> map = new ConcurrentHashMap<String, Val>();

  public <T> PropertyValue<T> obtain(String key, T defaultValue) {
    Val v = map.get(key);
    if (v == null) {
      Utils.checkArgument(defaultValue != null);
      Val ex = map.putIfAbsent(key, v = new Val<T>(key, defaultValue));
      if (ex != null) v = ex;
    } else {
      Utils.checkArgument(defaultValue != null && v.currentValue.getClass() == defaultValue.getClass());
    }
    return cast(v);
  }

  public void listen(PropertyListener<?> listener) {
    this.listeners.add(listener);
  }

  @SuppressWarnings("unchecked")
  private void fireAll(Val newValue, Object oldValue) {
    for (PropertyListener listener : listeners) {
      listener.fire(newValue, oldValue);
    }
  }

  private class Val<T> implements PropertyValue<T> {
    private volatile CopyOnWriteArrayList<PropertyListener<T>> listeners;
    private volatile T currentValue;
    private final String name;

    public Val(String name, T value) {
      this.name = name;
      this.currentValue = value;
    }

    public String name() {
      return this.name;
    }

    public T get() {
      return currentValue;
    }

    public synchronized void set(T newValue) {
      T oldValue = currentValue;
      this.currentValue = newValue;
      PropertyMapImpl.this.fireAll(this, oldValue);
      this.fireAll(this, oldValue);
    }

    private void fireAll(Val<T> newValue, T oldValue) {
      CopyOnWriteArrayList<PropertyListener<T>> listeners = this.listeners;
      if (listeners != null) {
        for (PropertyListener<T> listener : listeners) {
          listener.fire(newValue, oldValue);
        }
      }
    }

    public synchronized void listen(PropertyListener<T> listener) {
      if (listeners == null) listeners = new CopyOnWriteArrayList<PropertyListener<T>>();
      listeners.add(listener);
    }
  }
}
