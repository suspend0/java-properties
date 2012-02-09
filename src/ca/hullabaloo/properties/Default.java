package ca.hullabaloo.properties;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Apply this annotation to an interface method to provide a default return value
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Default {
  String value();
}
