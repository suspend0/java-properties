package ca.hullabaloo.properties;

import java.util.*;

/**
 * Binds types to property objects to make type-safe wrapper for the property data.
 *
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
        return newBuilder().add(p).build().create(type);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private Resolver props;

    private JavaProperties(Resolver props) {
        this.props = props;
    }

    /** Constructs an new type instance */
    public <T> T create(Class<T> type) {
        Converter standard = Converters.standard();
        Converter standardArrays = Converters.forArray(standard);
        Converter enums = Converters.enums();
        Converter enumArrays = Converters.forArray(enums);
        Converter converter = Converters.combine(standard,standardArrays, enums, enumArrays);
        return CglibPropertyImpl.create(type, props, converter);
    }

    public static class Builder {
        private List<Resolver> values = new ArrayList<Resolver>();

        public Builder add(Properties props) {
            values.add(Resolvers.viewOf(props));
            return this;
        }

        public Builder add(Map<String, ?> props) {
            values.add(Resolvers.viewOf(props));
            return this;
        }

        public Builder add(ResourceBundle props) {
            values.add(Resolvers.viewOf(props));
            return this;
        }

        JavaProperties build() {
            return new JavaProperties(Resolvers.and(values));
        }
    }
}
