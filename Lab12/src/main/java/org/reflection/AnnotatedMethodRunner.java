package org.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class AnnotatedMethodRunner {

    private static final int MOCK_INT = 42;

    public static void runAnnotatedMethods(List<Class<?>> publicClasses,
                                           List<Class<?>> annotationTypes) {
        publicClasses.forEach(clazz -> runMethodsInClass(clazz, annotationTypes));
    }

    private static void runMethodsInClass(Class<?> clazz, List<Class<?>> annotationTypes) {
        Arrays.stream(clazz.getDeclaredMethods())
              .filter(m -> !m.isSynthetic())
              .filter(m -> isAnnotatedWithAny(m, annotationTypes))
              .forEach(m -> invokeMethod(clazz, m));
    }

    private static boolean isAnnotatedWithAny(Method method, List<Class<?>> annotationTypes) {
        return annotationTypes.stream()
            .filter(Class::isAnnotation)
            .map(a -> (Class<? extends Annotation>) a)
            .anyMatch(method::isAnnotationPresent);
    }

    private static void invokeMethod(Class<?> clazz, Method method) {
        Class<?>[] params = method.getParameterTypes();
        boolean isNoArg    = params.length == 0;
        boolean isSingleInt = params.length == 1
                              && (params[0] == int.class || params[0] == Integer.class);

        // Skip methods with unsupported signatures
        if (!isNoArg && !isSingleInt) {
            System.out.println("  [skip]  " + clazz.getSimpleName() + "."
                               + method.getName() + " — unsupported signature");
            return;
        }

        try {
            method.setAccessible(true);

            // Static methods do not need an instance; others require a fresh one
            Object instance = Modifier.isStatic(method.getModifiers())
                ? null
                : clazz.getDeclaredConstructor().newInstance();

            Object result;
            if (isNoArg) {
                System.out.println("  → " + clazz.getSimpleName() + "." + method.getName() + "()");
                result = method.invoke(instance);
            } else {
                System.out.println("  → " + clazz.getSimpleName() + "." + method.getName()
                                   + "(" + MOCK_INT + ")  [mock int]");
                result = method.invoke(instance, MOCK_INT);
            }

            // Print non-void return values for visibility
            if (result != null) {
                System.out.println("     return: " + result);
            }

        } catch (InvocationTargetException e) {
            System.err.println("  [error] " + method.getName() + " threw: " + e.getCause());
        } catch (NoSuchMethodException e) {
            System.err.println("  [error] " + clazz.getSimpleName()
                               + " has no accessible no-arg constructor");
        } catch (Exception e) {
            System.err.println("  [error] Could not invoke " + method.getName()
                               + ": " + e.getMessage());
        }
    }
}
