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

        public void add(Properties props) {
            values.add(Resolvers.viewOf(props));
        }

        public void add(Map<String, ?> props) {
            values.add(Resolvers.viewOf(props));
        }

        public void add(ResourceBundle props) {
            values.add(Resolvers.viewOf(props));
        }

        JavaProperties build() {
            return new JavaProperties(Resolvers.and(values));
        }
    }
}
