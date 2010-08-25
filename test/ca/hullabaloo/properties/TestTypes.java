package ca.hullabaloo.properties;

import org.testng.annotations.DataProvider;

import java.util.Properties;

public class TestTypes {
    public static class TestClass implements Foo {
        public String getFoo() {
            return "13";
        }

        public int getBar() {
            return 13;
        }
    }

    public static class TestConstantClass implements Foo, Constants {
        public String getFoo() {
            return "13";
        }

        public int getBar() {
            return 13;
        }
    }

    public static abstract class TestAbstractMethod implements Foo {
        public abstract String getFoo();
    }

    public static abstract class TestConstantAbstractMethod implements Foo, Constants {
        public abstract String getFoo();
    }

    public static abstract class TestAbstractClass implements Foo {
        public String getFoo() {
            return "13";
        }

        public int getBar() {
            return 13;
        }
    }

    public static abstract class TestConstantAbstractClass implements Foo, Constants {
        public String getFoo() {
            return "13";
        }
    }

    public interface TestInterface extends Foo {
        public String getFoo();
    }

    public interface TestDefaultInterface extends Foo {
        @Default("13")
        public String getFoo();

        @Default("13")
        public int getBar();
    }

    public interface TestConstantInterface extends Foo, Constants {
        @Default("13")
        public String getFoo();
    }

    // =====TYPES above, PROVIDER below==================================================

    private static Object[][] wrapWithProps(Class... types) {
        Properties p = TestSupport.props(
                "foo", "31",
                "bar", "61"
        );
        Object[][] results = new Object[types.length][2];
        for (int i = 0; i < types.length; i++) {
            results[i][0] = p;
            results[i][1] = types[i];
        }
        return results;
    }


    private static Object[][] wrap(Class... types) {
        Object[][] results = new Object[types.length][1];
        for (int i = 0; i < types.length; i++)
            results[i][0] = types[i];
        return results;
    }

    public static final String
            ALL = "all",
            HAS_DEFAULT = "HAS_DEFAULT",
            MUTABLE = "MUTABLE",
            CONSTANT = "CONSTANT";

    @DataProvider(name = ALL)
    public static Object[][] all() {
        return wrapWithProps(
                TestClass.class, TestAbstractClass.class, TestAbstractMethod.class,
                TestDefaultInterface.class, TestInterface.class,
                TestConstantClass.class, TestConstantAbstractClass.class,
                TestConstantAbstractMethod.class, TestConstantInterface.class);
    }

    @DataProvider(name = HAS_DEFAULT)
    public static Object[][] hasDefault() {
        return wrap(TestClass.class, TestAbstractClass.class, TestDefaultInterface.class);
    }

    @DataProvider(name = MUTABLE)
    public static Object[][] mutable() {
        return wrapWithProps(
                TestClass.class, TestAbstractClass.class, TestAbstractMethod.class,
                TestDefaultInterface.class, TestInterface.class);
    }

    @DataProvider(name = CONSTANT)
    public static Object[][] constant() {
        return wrapWithProps(
                TestConstantClass.class, TestConstantAbstractClass.class,
                TestConstantAbstractMethod.class, TestConstantInterface.class);
    }
}
