package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EnumConverterTest {
    Converter instance = EnumConverter.instance();

    @Test
    public void testSupportsType() throws Exception {
        Assert.assertTrue(instance.supportsType(Baz.class));
        Assert.assertFalse(instance.supportsType(Integer.class));
    }

    @Test
    public void testConvert() throws Exception {
        Assert.assertEquals(instance.convert("EH", Baz.class), Baz.EH);
    }
}
