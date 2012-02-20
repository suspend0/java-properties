package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

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

  @Test
  public void obtainWithBadValues1() {
    // values from a random test which failed early versions
    PropertyMap m = new PropertyMapImpl();
    m.obtain("0.5121594190918518", 1);
    m.obtain("0.16755093468601145", 1);
    m.obtain("0.20120386447873528", 1);
    m.obtain("0.6807595952092049", 1);
  }

  @Test
  public void obtainWithBadValues2() {
    // values from a random test which failed early versions
    PropertyMap m = new PropertyMapImpl();
    m.obtain("0.5365660170455205", 1);
    m.obtain("0.08243370534329675", 1);
    m.obtain("0.7972700427963927", 1);
    m.obtain("0.7234455361782895", 1);
    m.obtain("0.08989733910761688", 1);
    m.obtain("0.9876396767073182", 1);
    m.obtain("0.60702558701895", 1);
    m.obtain("0.9319522352978661", 1);
    m.obtain("0.9030180496490398", 1);
    m.obtain("0.0950281579564004", 1);
    m.obtain("0.40130910326900104", 1);
  }

  @Test(timeOut = 1000)
  public void obtainTwiceUsingKeyWithNegativeHashCode() {
    PropertyMap m = new PropertyMapImpl();
    for (int i = 0; i < 100; i++) {
      String key = String.valueOf(Math.random());
      //System.out.printf("%s %s\n", i, key);
      m.obtain(key, 1);
    }
    String key = "negative hash code str";
    PropertyValue<Integer> v1 = m.obtain(key, 12);
    PropertyValue<Integer> v2 = m.obtain(key, 14);
    assertEquals(v1.get().intValue(), 12);
    assertEquals(v2.get().intValue(), 12);
    // strictly speaking, these don't need to be the same, but that assumption simplifies the listener testing
    assertSame(v1, v2);
  }

}
