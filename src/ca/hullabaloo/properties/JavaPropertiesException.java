package ca.hullabaloo.properties;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaPropertiesException extends IllegalArgumentException {
    static JavaPropertiesException nonPublicOrFinalClass(Class<?> type) {
        return create("Only public non-final classes are supported ", type);
    }

    public static JavaPropertiesException nonStaticInnerClass(Class<?> type) {
        return create("Non-static inner classes are not supported ", type);
    }

    private static JavaPropertiesException create(String format, Object... args) {
        return new JavaPropertiesException(format, args);
    }

    private final List<ErrorReport> messages = new ArrayList<ErrorReport>(2);

    JavaPropertiesException(String format, Object... args) {
        add(format, args);
    }

    public void unsupportedReturnType(Method m) {
        add("Unsupported return type for method %s", m);
    }

    public void missingProperty(String propertyName, Method propertyMethod) {
        add("Missing property '%s' to implement %s", propertyName, propertyMethod);
    }

    public void unknownError(Method m) {
        add("Unknown Error %s", m);
    }

    private void add(String format, Object... args) {
        messages.add(new ErrorReport(format, args));
    }

    @Override
    public String getMessage() {
        switch (messages.size()) {
            case 0:
                return super.getMessage();
            case 1:
                return messages.get(0).toString();
            default:
                StringBuilder b = new StringBuilder();
                b.append(messages.get(0).toString());
                int idx = 1;
                for (ErrorReport rpt : messages.subList(1, messages.size())) {
                    b.append("\n  ").append(idx++).append(". ").append(rpt);
                }
                return b.toString();
        }
    }

    private static class ErrorReport {
        private final String format;
        private final Object[] args;

        public ErrorReport(String format, Object[] args) {
            this.format = format;
            this.args = args;
        }

        public String toString() {
            return String.format(this.format, this.args);
        }
    }
}
