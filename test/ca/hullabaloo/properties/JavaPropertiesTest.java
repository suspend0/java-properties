package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

@Test
public class JavaPropertiesTest {
    public interface TestIf {
        @Default("13")
        String getFoo();
    }

    public void testInterfaceProperties() {
        TestIf instance = JavaProperties.newInstance().create(TestIf.class);
        Assert.assertEquals(instance.getFoo(), "13");
    }

    public void testInterfacePropertiesWithOverride() {
        Properties p = new Properties();
        p.setProperty("foo", "31");
        TestIf instance = JavaProperties.newInstance(p).create(TestIf.class);
        Assert.assertEquals(instance.getFoo(), "31");
    }
}
