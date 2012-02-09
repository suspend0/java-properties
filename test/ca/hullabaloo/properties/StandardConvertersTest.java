package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StandardConvertersTest {
  @Test
  public void testAll() throws Exception {
    Converter c = StandardConverters.all();

    Assert.assertTrue(c.convert("1", Integer.TYPE) == 1);
    Assert.assertEquals(c.convert("1", Integer.class), Integer.valueOf(1));
    Assert.assertTrue(c.convert("14.4", Float.TYPE) == 14.4f);
    Assert.assertEquals(c.convert("14.4", Float.class), 14.4f);
  }
}
