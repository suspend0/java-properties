package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Properties;

import static ca.hullabaloo.properties.TestSupport.bind;

@Test()
public class JavaPropertiesTest {
    @Test(dataProvider = TestTypes.HAS_DEFAULT, dataProviderClass = TestTypes.class)
    public void testDefault(Class<? extends Foo> type) {
        Foo instance = bind(type);
        Assert.assertEquals(instance.getFoo(), TestTypes.FOO_VAL);
        Assert.assertEquals(instance.getBar(), TestTypes.BAR_VAL);
        Assert.assertEquals(instance.getBazBaz(), 0.01d, TestTypes.BAZ_VAL);
    }

    @Test(dataProvider = TestTypes.ALL_FOO, dataProviderClass = TestTypes.class)
    public void testWithOverride(Properties p, Class<? extends Foo> type) {
        Foo instance = bind(type, p);
        Assert.assertEquals(instance.getFoo(), TestTypes.FOO_O);
        Assert.assertEquals(instance.getBar(), TestTypes.BAR_O);
        Assert.assertEquals(instance.getBazBaz(), 0.001d, TestTypes.BAZ_O);
    }

    @Test(dataProvider = TestTypes.ALL_BAR, dataProviderClass = TestTypes.class)
    public void testReturnTypes(Properties p, Class<? extends Bar> type) {
        Bar instance = bind(type, p);
        Assert.assertEquals(instance.getFoo(), TestTypes.FOO_O);
        Assert.assertEquals(instance.getBar(), TestTypes.BAR_O);
        Assert.assertEquals(instance.getBazBaz(), 0.001d, TestTypes.BAZ_O);
        Assert.assertEquals(instance.getLou(), TestTypes.LOU_O);
        Assert.assertEquals(instance.alice(), TestTypes.ALICE_O);
        Assert.assertEquals(instance.aunts(), TestTypes.AUNTS_O);
        Assert.assertTrue(Arrays.equals(instance.getUncles(), TestTypes.UNCLES_O));
        Assert.assertTrue(Arrays.equals(instance.isRahRahRah(), TestTypes.RAH_RAH_RAH_O));
        Assert.assertTrue(Arrays.equals(instance.go(), TestTypes.GO_O));
        Assert.assertTrue(Arrays.equals(instance.there(), TestTypes.THERE_O));
    }

    @Test(dataProvider = TestTypes.MUTABLE, dataProviderClass = TestTypes.class)
    public void testChangingOverride(Properties p, Class<? extends Foo> type) {
        p = (Properties) p.clone();
        Foo instance = TestSupport.bind(type, p);
        Assert.assertEquals(instance.getFoo(), TestTypes.FOO_O);
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
