package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

class BaseConverterTest {
  @Test
  public void supportsStringType() throws Exception {
    BaseConverter<String> instance = new BaseConverter<String>(String.class) {
      @Override
      protected String convert(String s) {
        return null;
      }
    };
    Assert.assertTrue(instance.supportsTarget(String.class));
  }

  @Test
  public void supportsStringArrayType() throws Exception {
    BaseConverter<String[]> instance = new BaseConverter<String[]>(String[].class) {
      @Override
      protected String[] convert(String s) {
        return null;
      }
    };
    Assert.assertTrue(instance.supportsTarget(String[].class));
  }
}
