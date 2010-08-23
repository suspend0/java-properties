package ca.hullabaloo.properties;

import org.testng.annotations.Test;

@Test
public class JavaPropertiesErrorTest {
    @Test(expectedExceptions = JavaPropertyException.class)
    public void testAbstractWithoutOverride() {
        CglibPropertyImpl.create(Resolvers.empty(), TestTypes.TestAbstractMethod.class);
    }

    @Test(expectedExceptions = JavaPropertyException.class)
    public void testInterfaceWithoutDefault() {
        CglibPropertyImpl.create(Resolvers.empty(), TestTypes.TestInterface.class);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static abstract class TestBadAbstract {
        abstract String getFoo();
    }

    @Test(expectedExceptions = JavaPropertyException.class)
    public void testAbstractWithNonPublicMethod() {
        CglibPropertyImpl.create(Resolvers.empty(), TestBadAbstract.class);
    }
}
