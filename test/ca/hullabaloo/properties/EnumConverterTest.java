package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EnumConverterTest {
  Converter instance = EnumConverter.INSTANCE;

  @Test
  public void testSupportsType() throws Exception {
    Assert.assertTrue(instance.supportsTarget(Baz.class));
    Assert.assertFalse(instance.supportsTarget(Integer.class));
  }

  @Test
  public void testConvert() throws Exception {
    Assert.assertEquals(instance.convert("EH", Baz.class), Baz.EH);
  }
}
