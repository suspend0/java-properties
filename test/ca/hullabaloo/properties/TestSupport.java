package ca.hullabaloo.properties;

import java.util.Properties;

public class TestSupport {
    public static <T> T bind(Class<T> type, String... keyValuePairs) {
        Properties p = props(keyValuePairs);
        return bind(type, p);
    }

    public static Properties props(String... keyValuePairs) {
        Properties p = new Properties();
        for (int i = 0; i < keyValuePairs.length;) {
            p.setProperty(keyValuePairs[i++], keyValuePairs[i++]);
        }
        return p;
    }

    public static <T> T bind(Class<T> type, Properties p) {
        return JavaProperties.newBuilder().add(p).build().create(type);
    }
}
