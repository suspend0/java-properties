package ca.hullabaloo.properties;

import java.lang.reflect.Method;
import java.util.Collection;

class Utils {
  static void checkArgument(boolean b) {
    if (!b) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Returns the property name to implement the method
   */
  public static String propertyName(Method m) {
    StringBuilder result = new StringBuilder();
    Namespace ns = m.getDeclaringClass().getAnnotation(Namespace.class);
    if (ns != null) {
      result.append(ns.value()).append('.');
    }
    char[] name = m.getName().toCharArray();
    int pos = 0;
    if (name[0] == 'g' && name[1] == 'e' && name[2] == 't') {
      pos = 3;
    } else if (name[0] == 'i' && name[1] == 's') {
      pos = 2;
    }
    result.append(Character.toLowerCase(name[pos++]));
    for (int N = name.length; pos < N; pos++) {
      char c = name[pos];
      if (Character.isUpperCase(c)) {
        result.append('.');
      }
      result.append(Character.toLowerCase(c));
    }

    return result.toString();
  }

  public static <T> boolean addIfAbsent(Collection<? super T> c, T item) {
    return !c.contains(item) && c.add(item);
  }
}
