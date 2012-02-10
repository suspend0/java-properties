package ca.hullabaloo.properties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

class PropertyMapImpl implements PropertyMap {
  @SuppressWarnings("unchecked")
  private static <T> PropertyValue<T> cast(Val v) {
    return (PropertyValue<T>) v;
  }

  @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
  private static void fireAll(CopyOnWriteArrayList listeners, String name, Object newValue, Object oldValue) {
    if (listeners != null) {
      Iterable<PropertyListener> tmp = listeners;
      for (PropertyListener propertyListener : tmp) {
        propertyListener.fire(name, newValue, oldValue);
      }
    }
  }

  private CopyOnWriteArrayList<PropertyListener<?>> listeners = new CopyOnWriteArrayList<PropertyListener<?>>();
  private ConcurrentMap<String, Val> map = new ConcurrentHashMap<String, Val>();

  @Override public <T> PropertyValue<T> obtain(String key, T defaultValue) {
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

  @Override public void listen(PropertyListener<?> listener) {
    this.listeners.add(listener);
  }

  private class Val<T> implements PropertyValue<T> {
    private volatile CopyOnWriteArrayList<PropertyListener<? super T>> listeners;
    private volatile T currentValue;
    private final String name;

    public Val(String name, T value) {
      this.name = name;
      this.currentValue = value;
    }

    @Override public T get() {
      return currentValue;
    }

    @Override public void set(T newValue) {
      T oldValue;
      synchronized (this) {
        oldValue = currentValue;
        this.currentValue = newValue;
      }
      fireAll(PropertyMapImpl.this.listeners, name, newValue, oldValue);
      fireAll(this.listeners, name, newValue, oldValue);
    }

    @Override public synchronized void listen(PropertyListener<? super T> listener) {
      if (listeners == null) listeners = new CopyOnWriteArrayList<PropertyListener<? super T>>();
      listeners.add(listener);
    }
  }
}
