package ca.hullabaloo.properties;

/** An adapter class to get property values from our supported input types */
interface Resolver {
    Object resolve(String name);
}
