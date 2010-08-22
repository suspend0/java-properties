package ca.hullabaloo.properties;

import java.util.Properties;

class Resolvers {
    public static Resolver viewOver(final Properties... properties) {
        return new Resolver() {
            public String resolve(String name) {
                for (Properties p : properties) {
                    String value = p.getProperty(name);
                    if (value != null)
                        return value;
                }
                return null;
            }
        };
    }
}
