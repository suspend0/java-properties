package ca.hullabaloo.properties;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaPropertiesException extends IllegalArgumentException {
    static Builder builder() {
        return new Builder();
    }

    static JavaPropertiesException nonPublicOrFinalClass(Class<?> type) {
        return builder().add("Only public non-final classes are supported ", type).build();
    }

    static JavaPropertiesException nonStaticInnerClass(Class<?> type) {
        return builder().add("Non-static inner classes are not supported ", type).build();
    }

    private final List<ErrorReport> messages = new ArrayList<ErrorReport>(2);

    private JavaPropertiesException(List<ErrorReport> errors) {
        messages.addAll(errors);
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

    public static class Builder {
        private Builder() {
        }

        private final List<ErrorReport> errors = new ArrayList<ErrorReport>();

        public void unsupportedReturnType(Method m) {
            add("Unsupported return type for method %s", m);
        }

        public void missingProperty(String propertyName, Method propertyMethod) {
            add("Missing property '%s' to implement %s", propertyName, propertyMethod);
        }

        public void unknownError(Method m) {
            add("Unknown Error %s", m);
        }

        public void conversionError(Method m, Object value, RuntimeException e) {
            add("Got exception [%s] when converting [%s] for method %s", e, value, m);
        }

        private Builder add(String format, Object... args) {
            errors.add(new ErrorReport(format, args));
            return this;
        }

        public JavaPropertiesException build() {
            JavaPropertiesException e = new JavaPropertiesException(errors);
            e.fillInStackTrace();
            StackTraceElement[] stack = e.getStackTrace();
            int skip = 0;
            while(getClass().getName().equals(stack[skip].getClassName())) {
                skip++;
            }
            if(skip > 0) {
                stack = Arrays.copyOfRange(stack,skip,stack.length);
                e.setStackTrace(stack);
            }
            throw e;
        }

        public void throwIfErrors(Class<?> type) {
            if (errors.isEmpty())
                return;

            add("Some methods could not be resolved on type %s", type);
            errors.add(0, errors.remove(errors.size() - 1));
            throw build();
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
