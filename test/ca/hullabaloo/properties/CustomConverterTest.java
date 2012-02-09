package ca.hullabaloo.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CustomConverterTest {
  @Test
  public void customConverter() {
    Map<String, String> props = new HashMap<String, String>();
    props.put("foo", "bar:1");
    P instance = JavaProperties.newBuilder().add(props).with(new OptionConverter()).build(P.class);
    Assert.assertEquals(instance.foo(), new Option("bar", 1));
  }

  public interface P {
    Option foo();
  }

  static class Option {
    final String name;
    final int position;

    Option(String name, int position) {
      this.position = position;
      this.name = name;
    }

    public static Option valueOf(String str) {
      String[] parts = str.split(":");
      if (parts.length != 2) {
        throw new IllegalArgumentException(str);
      }
      return new Option(parts[0], Integer.valueOf(parts[1]));
    }

    public boolean equals(Object o) {
      if (o instanceof Option) {
        Option that = (Option) o;
        return this.name.equals(that.name) && this.position == that.position;
      }
      return false;
    }

    public int hashCode() {
      return 31 + position * name.hashCode();
    }
  }

  static class OptionConverter extends BaseConverter<Option> {
    protected OptionConverter() {
      super(Option.class);
    }

    @Override
    protected Option convert(Object object) {
      if (object == null) {
        return null;
      }
      if (object instanceof String) {
        return Option.valueOf((String) object);
      }
      throw new IllegalArgumentException("could not convert " + object);
    }
  }
}
