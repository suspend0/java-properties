package ca.hullabaloo.properties;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

public class PropertyMapTest {
  @Test
  public void obtainAndRead() {
    PropertyMap m = new PropertyMapImpl();
    PropertyValue<Boolean> v = m.obtain("a", true);
    assertTrue(v.get());
  }

  @Test
  public void setValue() {
    PropertyMap m = new PropertyMapImpl();
    PropertyValue<Integer> v = m.obtain("b", 1);
    assertEquals(v.get().intValue(), 1);
    v.set(2);
    assertEquals(v.get().intValue(), 2);
  }

  @Test
  public void obtainTwice() {
    PropertyMap m = new PropertyMapImpl();
    PropertyValue<Integer> v1 = m.obtain("c", 12);
    PropertyValue<Integer> v2 = m.obtain("c", 14);
    assertEquals(v1.get().intValue(), 12);
    // strictly speaking, these don't need to be the same, but that assumption simplifies the listener testing
    assertSame(v1, v2);
  }

  @Test
  public void listenOnValue() {
    PropertyMap m = new PropertyMapImpl();
    PropertyValue<Integer> v1 = m.obtain("c", 12);
    PropertyValue<Integer> v2 = m.obtain("d", 14);
    L listener = new L();
    v1.listen(listener);

    // note we set values on ones w/o listeners too
    v1.set(24);
    v2.set(28);
    v1.set(25);
    v2.set(29);

    assertEquals(listener.names, asList("c", "c"));
    assertEquals(listener.newValues, asList(24, 25));
    assertEquals(listener.oldValues, asList(12, 24));
  }

  @Test
  public void listenOnMap() {
    PropertyMap m = new PropertyMapImpl();
    PropertyValue<Integer> v1 = m.obtain("c", 12);
    PropertyValue<Integer> v2 = m.obtain("d", 14);
    L listener = new L();
    m.listen(listener);

    v1.set(24);
    v2.set(28);
    v1.set(25);
    v2.set(29);

    assertEquals(listener.names, asList("c", "d", "c", "d"));
    assertEquals(listener.newValues, asList(24, 28, 25, 29));
    assertEquals(listener.oldValues, asList(12, 14, 24, 28));
  }

  static class L implements PropertyListener<Integer> {
    List<String> names = new ArrayList<String>();
    List<Integer> oldValues = new ArrayList<Integer>();
    List<Integer> newValues = new ArrayList<Integer>();

    public void fire(PropertyValue<Integer> newValue, Integer oldValue) {
      names.add(newValue.name());
      newValues.add(newValue.get());
      oldValues.add(oldValue);
    }
  }
}
