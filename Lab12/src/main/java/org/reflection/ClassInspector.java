package org.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassInspector {

    public static List<Class<?>> findAnnotationTypes(List<Class<?>> classes) {
        return classes.stream()
                      .filter(Class::isAnnotation)
                      .toList();
    }

    public static List<Class<?>> findPublicClasses(List<Class<?>> classes) {
        return classes.stream()
                      .filter(c -> !c.isAnnotation())
                      .filter(c -> Modifier.isPublic(c.getModifiers()))
                      .toList();
    }

    public static void printPrototype(Class<?> clazz) {
        System.out.println("┌─── " + clazz.getName());
        System.out.println("│  modifiers : " + Modifier.toString(clazz.getModifiers()));

        // Superclass — omit Object since every class implicitly extends it
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            System.out.println("│  extends   : " + clazz.getSuperclass().getName());
        }

        // Implemented interfaces
        Class<?>[] ifaces = clazz.getInterfaces();
        if (ifaces.length > 0) {
            String names = Arrays.stream(ifaces)
                                 .map(Class::getName)
                                 .collect(Collectors.joining(", "));
            System.out.println("│  implements: " + names);
        }

        // Declared fields (including private)
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            System.out.println("│  fields:");
            Arrays.stream(fields).forEach(f ->
                System.out.println("│    " + Modifier.toString(f.getModifiers())
                                   + " " + f.getType().getSimpleName()
                                   + " " + f.getName())
            );
        }

        // Declared constructors
        Constructor<?>[] ctors = clazz.getDeclaredConstructors();
        if (ctors.length > 0) {
            System.out.println("│  constructors:");
            Arrays.stream(ctors).forEach(c ->
                System.out.println("│    " + Modifier.toString(c.getModifiers())
                                   + " " + c.getDeclaringClass().getSimpleName()
                                   + "(" + formatTypes(c.getParameterTypes()) + ")")
            );
        }

        // Declared methods (including private, synthetic methods are filtered out)
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            System.out.println("│  methods:");
            Arrays.stream(methods)
                  .filter(m -> !m.isSynthetic())
                  .forEach(m ->
                      System.out.println("│    " + Modifier.toString(m.getModifiers())
                                         + " " + m.getReturnType().getSimpleName()
                                         + " " + m.getName()
                                         + "(" + formatTypes(m.getParameterTypes()) + ")")
                  );
        }

        System.out.println("└────────────────────────────────────\n");
    }

    private static String formatTypes(Class<?>[] types) {
        return Arrays.stream(types)
                     .map(Class::getSimpleName)
                     .collect(Collectors.joining(", "));
    }
}
