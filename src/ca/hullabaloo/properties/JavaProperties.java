package ca.hullabaloo.properties;

import java.util.*;

public class JavaProperties {
    private Resolver props;

    private JavaProperties(Resolver props) {
        this.props = props;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public <T> T create(Class<T> type) {
        if (type.isInterface()) {
            return JdkProxyPropertyImpl.create(props, type);
        }
        return CglibPropertyImpl.create(props, type);
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
