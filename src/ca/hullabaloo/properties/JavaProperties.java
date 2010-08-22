package ca.hullabaloo.properties;

import java.lang.reflect.Modifier;
import java.util.*;

public class JavaProperties {
    private Resolver props;

    private JavaProperties(Resolver props) {
        this.props = props;
    }

    public static JavaProperties newInstance(Properties... properties) {
        return new JavaProperties(Resolvers.viewOver(properties));
    }

    public <T> T create(Class<T> type) {
        if (type.isInterface()) {
            return InterfaceProperties.create(props, type);
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException();
        }
        if (Modifier.isFinal(type.getModifiers())) {
            throw new IllegalArgumentException();
        }
        return ClassProperties.create(props,type);
    }
}
