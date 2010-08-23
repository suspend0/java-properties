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

    public void testProperties() {
        TestIf instance = JdkProxyPropertyImpl.create(Resolvers.empty(), TestIf.class);
        Assert.assertEquals(instance.getFoo(), "13");
    }

    public void testWithOverride() {
        Properties p = new Properties();
        p.setProperty("foo", "31");
        TestIf instance = JdkProxyPropertyImpl.create(Resolvers.viewOf(p), TestIf.class);
        Assert.assertEquals(instance.getFoo(), "31");
    }

    public void testChangingOverride() {
        Properties p = new Properties();
        p.setProperty("foo","31");
        TestIf instance = JdkProxyPropertyImpl.create(Resolvers.viewOf(p), TestIf.class);
        Assert.assertEquals(instance.getFoo(), "31");
        p.setProperty("foo","41");
        Assert.assertEquals(instance.getFoo(), "41");
    }

    public interface TestConstantIf extends Constants {
        String foo();
    }

    public void testConstantsOverride() {
        Properties p = new Properties();
        p.setProperty("foo","31");
        TestConstantIf instance = JdkProxyPropertyImpl.create(Resolvers.viewOf(p), TestConstantIf.class);
        Assert.assertEquals(instance.foo(), "31");
        p.setProperty("foo","41");
        Assert.assertEquals(instance.foo(), "31");
    }

}
