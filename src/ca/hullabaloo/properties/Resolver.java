package ca.hullabaloo.properties;

/**
 * An adapter class to get property values from our supported input types
 */
interface Resolver {
  String resolve(String name);
}
