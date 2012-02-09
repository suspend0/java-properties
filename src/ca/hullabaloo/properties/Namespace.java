package ca.hullabaloo.properties;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Prefixes the name used to resolve properties.
 * <p/>
 * <table>
 * <tr><th>Namespace</th><th>Method</th><th>Property</th></tr>
 * <tr><td>-</td><td>getFoo()</td><td>foo</td></tr>
 * <tr><td>-</td><td>getFooBar()</td><td>foo.bar</td></tr>
 * <tr><td>corp.app</td><td>getFoo()</td><td>corp.app.foo</td></tr>
 * <tr><td>corp.app</td><td>getFoo()</td><td>corp.app.foo.bar</td></tr>
 * </table>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Namespace {
  String value() default "";
}
