package ca.hullabaloo.properties;

import java.lang.reflect.Modifier;
import java.util.*;

public class JavaProperties {
    private Resolver props;

    private JavaProperties(Resolver props) {
        this.props = props;
    }

    public static JavaProperties newBuilder(Properties... properties) {
        return new JavaProperties(Resolvers.viewOver(properties));
    }

    public <T> T create(Class<T> type) {
        if (type.isInterface()) {
            return JdkProxyPropertyImpl.create(props, type);
        }
        return CglibPropertyImpl.create(props,type);
    }
}
