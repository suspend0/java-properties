package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BaseConverterTest {
    private BaseConverter<String> instance = new BaseConverter<String>() {
        @Override
        protected String convert(Object object) {
            return null;
        }
    };

    @Test
    public void testSupportsType() throws Exception {
        Assert.assertTrue(instance.supportsType(String.class));
    }
}
