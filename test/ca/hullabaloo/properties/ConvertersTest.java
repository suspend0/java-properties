package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ConvertersTest {
    @Test
    public void testCombine() throws Exception {
        Converter a = new Converter() {
            public boolean supportsTarget(Class<?> type) {
                return type == String.class;
            }

            public <T> T convert(Object object, Class<T> targetType) {
                return targetType.cast("x");
            }
        };
        Converter b = new Converter() {
            public boolean supportsTarget(Class<?> type) {
                return type == Integer.class;
            }

            public <T> T convert(Object object, Class<T> targetType) {
                return targetType.cast(1);
            }
        };

        Converter instance = Converters.combine(a, b);
        Assert.assertTrue(instance.supportsTarget(String.class));
        Assert.assertTrue(instance.supportsTarget(Integer.class));
        Assert.assertFalse(instance.supportsTarget(Float.class));
        Assert.assertEquals(instance.convert(new Object(), String.class), "x");
        Assert.assertEquals(instance.convert(new Object(), Integer.class), Integer.valueOf(1));
    }
}
