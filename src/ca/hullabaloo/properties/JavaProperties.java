package ca.hullabaloo.properties;

import java.util.*;

/**
 * Binds types to property objects to make type-safe wrapper for the property data.
 * <p/>
 * <pre>
 * Properties props = new Properties();
 * props.put("name", "Joe");
 *
 * public interface Hello {
 *   getName();
 * }
 *
 * Hello instance = JavaProperties.bind(Hello.class,props);
 * instance.getName(); // returns "Joe"
 * </pre>
 *
 * @see ca.hullabaloo.properties.Constants
 * @see ca.hullabaloo.properties.Converter
 * @see ca.hullabaloo.properties.Namespace
 * @see ca.hullabaloo.properties.Default
 */
public class JavaProperties {
  /**
   * By default, the property values are returned from the map
   * on each method invocation.  You can implement marker interface
   * {@link ca.hullabaloo.properties.Constants} for different behavior.
   */
  public static <T> T bind(Class<T> type, Properties p) {
    return newBuilder().add(p).build(type);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private final Resolver props;
  private final Converter converter;

  private JavaProperties(Resolver props, Converter converter) {
    this.props = props;
    this.converter = converter;
  }

  /**
   * Constructs an new type instance
   */
  private <T> T create(Class<T> type) {
    return CglibPropertyImpl.create(type, props, converter);
  }

  public static class Builder {
    private List<Resolver> values = new ArrayList<Resolver>();
    private final List<Converter> converters = new ArrayList<Converter>();

    public Builder add(Properties props) {
      values.add(Resolvers.viewOf(props));
      return this;
    }

    public Builder add(Map<String, String> props) {
      values.add(Resolvers.viewOf(props));
      return this;
    }

    public Builder add(ResourceBundle props) {
      values.add(Resolvers.viewOf(props));
      return this;
    }

    Builder with(Converter converter) {
      this.converters.add(converter);
      return this;
    }

    public <T> T build(Class<T> type) {
      Converter standard = Converters.standard();
      Converter standardArrays = Converters.forArray(standard);
      Converter enums = Converters.enums();
      Converter enumArrays = Converters.forArray(enums);

      Utils.addIfAbsent(this.converters, standard);
      Utils.addIfAbsent(this.converters, standardArrays);
      Utils.addIfAbsent(this.converters, enums);
      Utils.addIfAbsent(this.converters, enumArrays);

      Resolver resolver = Resolvers.and(values);
      Converter converter = Converters.combine(converters);
      return new JavaProperties(resolver, converter).create(type);
    }
  }
}
