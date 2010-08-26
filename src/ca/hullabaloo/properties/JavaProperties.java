package ca.hullabaloo.properties;

import java.util.*;

public class JavaProperties {
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

    public <T> T create(Class<T> type) {
        Converter standard = StandardConverters.all();
        Converter standardArrays = ArrayConverter.create(standard);
        Converter converter = Converters.combine(standard,standardArrays);
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
