package ca.hullabaloo.properties;

import org.testng.annotations.Test;

import java.util.Properties;

@Test
public class JavaPropertiesErrorTest {
    @Test(expectedExceptions = JavaPropertiesException.class)
    public void testAbstractWithoutOverride() {
        JavaProperties.bind(TestTypes.TestAbstractMethod.class, new Properties());
    }

    @Test(expectedExceptions = JavaPropertiesException.class)
    public void testInterfaceWithoutDefault() {
        JavaProperties.bind(TestTypes.TestInterface.class, new Properties());
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public interface BadTypeDefault {
        @Default("bacon")
        public int howMuchDoWeLoveIt();
    }

    @Test(expectedExceptions = JavaPropertiesException.class)
    public void testCannotParseDefaults() {
        JavaProperties.bind(BadTypeDefault.class, new Properties());
    }
}
