package ca.hullabaloo.properties;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class JavaPropertiesException extends IllegalArgumentException {
  static Builder builder() {
    return new Builder();
  }

  static JavaPropertiesException nonPublicOrFinalClass(Class<?> type) {
    return builder().add(format("Only public non-final classes are supported [%s]", type)).build();
  }

  static JavaPropertiesException nonStaticInnerClass(Class<?> type) {
    return builder().add(format("Non-static inner classes are not supported [%s]", type)).build();
  }

  private final List<ErrorReport> causes;

  private JavaPropertiesException(List<ErrorReport> errors) {
    this.causes = errors;
  }

  @Override
  public void printStackTrace(PrintStream s) {
    new StackPrinter<PrintStream>(s).go();
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    new StackPrinter<PrintWriter>(s).go();
  }

  public static class Builder {
    private Builder() {
    }

    private final List<ErrorReport> errors = new ArrayList<ErrorReport>();

    public void unsupportedReturnType(Method m) {
      add(format("Unsupported return type for method %s", m));
    }

    public void missingProperty(String propertyName, Method propertyMethod) {
      add(format("Missing property '%s' to implement %s", propertyName, propertyMethod));
    }

    public void unknownError(Method m) {
      add(format("Unknown Error %s", m));
    }

    public void conversionError(Method m, Object value, RuntimeException e) {
      add(format("Got exception [%s] when converting [%s] for method %s", e, value, m), e);
    }

    private Builder add(String message) {
      return add(message, null);
    }

    private Builder add(String message, Throwable cause) {
      errors.add(new ErrorReport(message, cause));
      return this;
    }

    public JavaPropertiesException build() {
      JavaPropertiesException e = new JavaPropertiesException(errors);
      e.fillInStackTrace();
      StackTraceElement[] stack = e.getStackTrace();
      int skip = 0;
      while (getClass().getName().equals(stack[skip].getClassName())) {
        skip++;
      }
      if (skip > 0) {
        stack = Arrays.copyOfRange(stack, skip, stack.length);
        e.setStackTrace(stack);
      }
      throw e;
    }

    public void throwIfErrors(Class<?> type) {
      if (errors.isEmpty()) {
        return;
      }

      add(format("Some methods could not be resolved on type %s", type));
      errors.add(0, errors.remove(errors.size() - 1));
      throw build();
    }
  }

  private static class ErrorReport {
    private final String message;
    private final Throwable cause;

    private ErrorReport(String message, Throwable cause) {
      this.message = message;
      this.cause = cause;
    }
  }

  private class StackPrinter<P extends Appendable & Flushable> {
    private String lineSeparator = (String) java.security.AccessController.doPrivileged(
        new sun.security.action.GetPropertyAction("line.separator"));
    private final P out;

    private StackPrinter(P out) {
      this.out = out;
    }

    public void go() {
      synchronized (this.out) {
        try {
          print();
          this.out.flush();
        } catch (IOException e) {
          throw new Error(e); // wrapped types do not throw
        }
      }
    }

    private void print() throws IOException {
      println(JavaPropertiesException.this);
      StackTraceElement[] trace = getStackTrace();
      for (StackTraceElement frame : trace) {
        println("\tat " + frame);
      }

      int c = 0;
      for (ErrorReport cause : JavaPropertiesException.this.causes) {
        println(String.format("=>Cause #%d %s", ++c, cause.message));
        if (cause.cause != null) {
          printAsCause(cause.cause, trace);
        }
      }
    }

    /**
     * Print our stack trace as a cause for the specified stack trace.
     */
    private void printAsCause(Throwable cause, StackTraceElement[] causedTrace) throws IOException {
      // Compute number of frames in common between this and caused
      StackTraceElement[] trace = cause.getStackTrace();
      int m = trace.length - 1, n = causedTrace.length - 1;
      while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
        m--;
        n--;
      }
      int framesInCommon = trace.length - 1 - m;

      for (int i = 0; i <= m; i++) {
        println("\tat " + trace[i]);
      }
      if (framesInCommon != 0) {
        println("\t... " + framesInCommon + " more");
      }

      // Recurse if we have a cause
      Throwable nextCause = cause.getCause();
      if (nextCause != null) {
        printAsCause(nextCause, trace);
      }
    }

    private void println(Object o) throws IOException {
      out.append(String.valueOf(o)).append(lineSeparator);
    }
  }
}
