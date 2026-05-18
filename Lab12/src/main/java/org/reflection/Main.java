package org.reflection;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        String className = "org.example.Task";

        try {
            System.out.println("Attempting to load class: " + className);
            Class<?> loadedClass = Class.forName(className);
            System.out.println("Class loaded successfully.");

            Object classInstance = loadedClass.getDeclaredConstructor().newInstance();

            Method runMethod;
            try {
                runMethod = loadedClass.getDeclaredMethod("run");
                System.out.println("Found method: run()");
            } catch (NoSuchMethodException e) {
                System.out.println("The class does not contain run().");
                return;
            }

            if (runMethod != null) {
                runMethod.setAccessible(true);
                System.out.println("--Start--");
                runMethod.invoke(classInstance);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Could not find the class.");
        } catch (Exception e) {
            System.err.println("An error occurred during reflection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
