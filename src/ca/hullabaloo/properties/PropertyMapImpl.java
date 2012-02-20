package ca.hullabaloo.properties;

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
        propertyListener.hear(name, newValue, oldValue);
      }
    }
  }

  private CopyOnWriteArrayList<PropertyListener<?>> listeners = new CopyOnWriteArrayList<PropertyListener<?>>();
  private Hash data = new Hash();

  @Override public <T> PropertyValue<T> obtain(String key, T defaultValue) {
    Utils.checkArgument(defaultValue != null);
    Val v = data.get(key);
    if (v == null) {
      v = data.putIfAbsent(new Val<T>(key, defaultValue));
    } else {
      assert defaultValue != null;
      Utils.checkArgument(v.currentValue.getClass() == defaultValue.getClass());
    }
    return cast(v);
  }

  @Override public void listen(PropertyListener<?> listener) {
    this.listeners.add(listener);
  }

  private class Val<T> implements PropertyValue<T> {
    // guarded by 'this'; initialized to 'null'
    private volatile CopyOnWriteArrayList<PropertyListener<? super T>> listeners;
    // unguarded, never null
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

  private static final class Hash {
    private volatile Val[] data = new Val[4];
    private int size = 0;

    private static int slot(String key, Val[] data) {return key.hashCode() & (data.length - 1);}

    private static int nextSlot(int slot, Val[] data) {return (slot + 1) & (data.length - 1);}

    public Val get(String key) {
      Val[] data = this.data;
      Val r;
      int slot = slot(key, data);
      while ((r = data[slot]) != null) {
        if (r.name.equals(key)) {
          return r;
        }
        slot = nextSlot(slot, data);
      }
      return r;
    }

    public synchronized Val putIfAbsent(Val val) {
      Val[] data = this.data;
      if (data.length / 2 <= size) {
        data = resize();
      }
      Val r = putImpl(data, val);
      if (r == val) size++;
      return r;
    }

    private static Val putImpl(Val[] data, Val val) {
      Val r;
      int slot = slot(val.name, data);
      while ((r = data[slot]) != null) {
        if (val.name.hashCode() == r.name.hashCode() && val.name.equals(r.name)) {
          return r;
        }
        slot = nextSlot(slot, data);
      }
      data[slot] = val;
      return val;
    }

    private Val[] resize() {
      Val[] oldA = this.data;
      Val[] newA = new Val[this.data.length * 2];
      for (Val v : oldA) {
        if (v != null) {
          putImpl(newA, v);
        }
      }
      // since reads are not synchronized, we only publish the
      // new array once resize is complete.  Puts are synchronized.
      return (this.data = newA);
    }
  }
}
