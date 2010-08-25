package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

import static ca.hullabaloo.properties.TestSupport.bind;

@Test()
public class JavaPropertiesTest {
    @Test(dataProvider = TestTypes.HAS_DEFAULT, dataProviderClass = TestTypes.class)
    public void testDefault(Class<? extends Foo> type) {
        Foo instance = bind(type);
        Assert.assertEquals(instance.getFoo(), "13");
        Assert.assertEquals(instance.getBar(), 13);
    }

    @Test(dataProvider = TestTypes.ALL, dataProviderClass = TestTypes.class)
    public void testWithOverride(Properties p, Class<? extends Foo> type) {
        Foo instance = bind(type, p);
        Assert.assertEquals(instance.getFoo(), "31");
        Assert.assertEquals(instance.getBar(), 61);
    }

    @Test(dataProvider = TestTypes.MUTABLE, dataProviderClass = TestTypes.class)
    public void testChangingOverride(Properties p, Class<? extends Foo> type) {
        p = (Properties) p.clone();
        Foo instance = TestSupport.bind(type, p);
        Assert.assertEquals(instance.getFoo(), "31");
        p.setProperty("foo", "41");
        Assert.assertEquals(instance.getFoo(), "41");
    }

    @Test(dataProvider = TestTypes.CONSTANT, dataProviderClass = TestTypes.class)
    public void testConstantOverride(Properties p, Class<? extends Foo> type) {
        p = (Properties) p.clone();
        Foo instance = TestSupport.bind(type, p);
        Assert.assertEquals(instance.getFoo(), "31");
        p.setProperty("foo", "41");
        Assert.assertEquals(instance.getFoo(), "31");
    }
}
