package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

@Test
public class JdkProxyPropertyImplTest {
    public interface TestIf {
        @Default("13")
        String getFoo();
    }

    public void testInterfaceProperties() {
        TestIf instance = JdkProxyPropertyImpl.create(Resolvers.viewOver(), TestIf.class);
        Assert.assertEquals(instance.getFoo(), "13");
    }

    public void testInterfacePropertiesWithOverride() {
        Properties p = new Properties();
        p.setProperty("foo", "31");
        TestIf instance = JdkProxyPropertyImpl.create(Resolvers.viewOver(p), TestIf.class);
        Assert.assertEquals(instance.getFoo(), "31");
    }
}
