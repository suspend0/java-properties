package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

class BaseConverterTest {
    @Test
    public void supportsStringType() throws Exception {
        BaseConverter<String> instance = new BaseConverter<String>() {
            @Override
            protected String convert(Object object) {
                return null;
            }
        };
        Assert.assertTrue(instance.supportsType(String.class));
    }

    @Test
    public void supportsStringArrayType() throws Exception {
        BaseConverter<String[]> instance = new BaseConverter<String[]>() {
            @Override
            protected String[] convert(Object object) {
                return null;
            }
        };
        Assert.assertTrue(instance.supportsType(String[].class));
    }
}
