package ca.hullabaloo.properties;

/**
 * Apply this interface for the types you send to {@link JavaProperties} and
 * the binder will copy the values into the constructed type.  Without this
 * marker interface, the values will be retrieved from their underlying
 * map each method invocation.
 *
 * @TODO maybe this should be the default, and we should have LiveValues or something
 * @see ca.hullabaloo.properties.JavaProperties
 */
public interface Constants {
}
