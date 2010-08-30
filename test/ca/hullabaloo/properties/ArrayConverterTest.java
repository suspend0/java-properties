package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

public class ArrayConverterTest {
    private ArrayConverter instance = ArrayConverter.wrap(StandardConverters.all());

    @Test
    public void convertString() {
        Assert.assertEquals(instance.convert("[a,b]", String[].class), new String[]{"a", "b"});
        Assert.assertEquals(instance.convert("[a, b]", String[].class), new String[]{"a", "b"});
        Assert.assertTrue(Arrays.equals(instance.convert("[2,3]", int[].class), new int[]{2, 3}));
    }

    @Test
    void convertArray() {
        Assert.assertEquals(instance.convert(new String[]{"a", "b"}, String[].class), new String[]{"a", "b"});
        Assert.assertTrue(Arrays.equals(instance.convert(new int[]{2, 3}, int[].class), new int[]{2, 3}));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void badConvert() {
        instance.convert("butter", String[].class);
    }
}
