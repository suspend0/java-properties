package ca.hullabaloo.properties;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class Utils {
    static void checkArgument(boolean b) {
        if (!b) throw new IllegalArgumentException();
    }

    static String rootOf(String methodName) {
        if (methodName.startsWith("get"))
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        if (methodName.startsWith("is"))
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        return methodName;
    }

    public static <T> String namespaceOf(Class<T> type) {
        Namespace n = type.getAnnotation(Namespace.class);
        return n == null ? "" : n.value();
    }

    public static String propertyName(Method m) {
        StringBuilder result = new StringBuilder();
        Namespace ns = m.getDeclaringClass().getAnnotation(Namespace.class);
        if (ns != null)
            result.append(ns.value()).append('.');
        char[] name = m.getName().toCharArray();
        int pos = 0;
        if (name[0] == 'g' && name[1] == 'e' && name[2] == 't')
            pos = 3;
        else if (name[0] == 'i' && name[1] == 's')
            pos = 2;
        result.append(Character.toLowerCase(name[pos++]));
        for (int N = name.length; pos < N; pos++) {
            char c = name[pos];
            if (Character.isUpperCase(c))
                result.append('.');
            result.append(Character.toLowerCase(c));
        }

        return result.toString();
    }

    public static Iterable<Method> publicNonFinalDeclaredMethods(Class<?> type) {
        List<Method> result = Arrays.asList(type.getDeclaredMethods());
        result = new ArrayList<Method>(result);
        for (Iterator<Method> i = result.iterator(); i.hasNext();) {
            int modifiers = i.next().getModifiers();
            boolean publicAndNotFinal = Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers);
            if (!publicAndNotFinal)
                i.remove();
        }
        return result;
    }
}
