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
    }

    @Test(dataProvider = TestTypes.ALL, dataProviderClass = TestTypes.class)
    public void testWithOverride(Class<? extends Foo> type) {
        Foo instance = bind(type, "foo", "31");
        Assert.assertEquals(instance.getFoo(), "31");
    }

    @Test(dataProvider = TestTypes.MUTABLE, dataProviderClass = TestTypes.class)
    public void testChangingOverride(Class<? extends Foo> type) {
        Properties p = TestSupport.props("foo", "31");
        Foo instance = TestSupport.bind(type, p);
        Assert.assertEquals(instance.getFoo(), "31");
        p.setProperty("foo", "41");
        Assert.assertEquals(instance.getFoo(), "41");
    }

    @Test(dataProvider = TestTypes.CONSTANT, dataProviderClass = TestTypes.class)
    public void testConstantOverride(Class<? extends Foo> type) {
        Properties p = TestSupport.props("foo", "31");
        Foo instance = TestSupport.bind(type, p);
        Assert.assertEquals(instance.getFoo(), "31");
        p.setProperty("foo", "41");
        Assert.assertEquals(instance.getFoo(), "31");
    }
}
