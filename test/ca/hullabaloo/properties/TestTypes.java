package ca.hullabaloo.properties;

import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.Properties;

public class TestTypes {
    //
    static final String FOO_VAL = "13";
    static final String FOO_S = "13";
    static final String FOO_O = "31";

    //
    static final int BAR_VAL = 133;
    static final String BAR_S = "133";
    static final int BAR_O = 313;

    //
    static final double BAZ_VAL = 1.33d;
    static final String BAZ_S = "1.33";
    static final double BAZ_O = 3.13d;

    //
    static final long LOU_O = 3133L;
    static final float ALICE_O = 31.33f;
    public static final String[] AUNTS_O = {"b","c"};
    public static final int[] UNCLES_O = {87};
    public static final double[] RAH_RAH_RAH_O = {83.33d};
    public static final long[] GO_O = {33223L};
    public static final float[] THERE_O = {18.3f};
    public static final Baz WAHOOIE_O = Baz.BEE;
    public static final Baz[] WAHOOIES_O = {Baz.EH,Baz.BEE};

    public static class TestClass implements Bar {
        public String getFoo() {
            return FOO_VAL;
        }

        public int getBar() {
            return BAR_VAL;
        }

        public double getBazBaz() {
            return BAZ_VAL;
        }

        public long getLou() {
            return 0;
        }

        public float alice() {
            return 0f;
        }

        public String[] aunts() {
            return null;
        }

        public int[] getUncles() {
            return null;
        }

        public double[] isRahRahRah() {
            return null;
        }

        public long[] go() {
            return null;
        }

        public float[] there() {
            return null;
        }

        public Baz wahooie() {
            return null;
        }

        public Baz[] wahooies() {
            return null;
        }
    }

    public static class TestConstantClass implements Foo, Constants {
        public String getFoo() {
            return FOO_VAL;
        }

        public int getBar() {
            return BAR_VAL;
        }

        public double getBazBaz() {
            return BAZ_VAL;
        }
    }

    public static abstract class TestAbstractMethod implements Foo {
        public abstract String getFoo();
    }

    public static abstract class TestConstantAbstractMethod implements Foo, Constants {
        public abstract String getFoo();

        public abstract int getBar();

        public abstract double getBazBaz();
    }

    public static abstract class TestAbstractClass implements Foo {
        public String getFoo() {
            return FOO_VAL;
        }

        public int getBar() {
            return BAR_VAL;
        }

        public double getBazBaz() {
            return BAZ_VAL;
        }
    }

    public static abstract class TestConstantAbstractClass implements Foo, Constants {
    }

    public interface TestInterface extends Bar {
    }

    public interface TestDefaultInterface extends Foo {
        @Default(FOO_S)
        public String getFoo();

        @Default(BAR_S)
        public int getBar();

        @Default(BAZ_S)
        public double getBazBaz();
    }

    public interface TestConstantInterface extends Foo, Constants {
        @Default(FOO_VAL)
        public String getFoo();
    }

    // =====TYPES above, PROVIDER below==================================================

    private static Object[][] wrapWithProps(Class... types) {
        Properties p = TestSupport.props(
                "foo", String.valueOf(FOO_O),
                "bar", String.valueOf(BAR_O),
                "baz.baz", String.valueOf(BAZ_O),
                "lou", String.valueOf(LOU_O),
                "alice", String.valueOf(ALICE_O),
                "aunts", Arrays.toString(AUNTS_O),
                "uncles", Arrays.toString(UNCLES_O),
                "rah.rah.rah", Arrays.toString(RAH_RAH_RAH_O),
                "go", Arrays.toString(GO_O),
                "there", Arrays.toString(THERE_O),
                "wahooie", String.valueOf(WAHOOIE_O),
                "wahooies", Arrays.toString(WAHOOIES_O)
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
            ALL_BAR = "all-bar",
            ALL_FOO = "all-foo",
            HAS_DEFAULT = "HAS_DEFAULT",
            MUTABLE = "MUTABLE",
            CONSTANT = "CONSTANT";

    @DataProvider(name = ALL_BAR)
    public static Object[][] allBar() {
        return wrapWithProps(TestClass.class,TestInterface.class);
    }

    @DataProvider(name = ALL_FOO)
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
