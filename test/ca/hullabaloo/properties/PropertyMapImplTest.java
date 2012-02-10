package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PropertyMapImplTest {
  @Test
  public void testObtain() throws Exception {
    Map<Integer, PropertyValue<Integer>> actuals = new HashMap<Integer, PropertyValue<Integer>>();

    PropertyMap m = new PropertyMapImpl();
    for (int i = 'a'; i <= 'z'; i++) {
      String key = String.valueOf(i);
      PropertyValue<Integer> v = m.obtain(key, i);
      actuals.put(i, v);
    }
    for (int i = 'a'; i <= 'z'; i++) {
      String key = String.valueOf(i);
      PropertyValue<Integer> v = m.obtain(key, i);
      Assert.assertSame(v, actuals.remove(i));
    }
  }
}
