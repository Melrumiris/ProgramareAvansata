package org.reflection;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ClassFileLoader {

    public static List<Class<?>> loadClasses(File rootFolder) throws Exception {
        URL[] urls = { rootFolder.toURI().toURL() };
        // Use the system class loader as parent so java.lang, java.util etc. are still resolved
        URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

        List<String> classNames = collectClassNames(rootFolder, rootFolder);
        List<Class<?>> classes = new ArrayList<>();

        for (String name : classNames) {
            try {
                classes.add(loader.loadClass(name));
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                System.err.println("  [warn] Could not load: " + name + " — " + e.getMessage());
            }
        }

        return classes;
    }

    private static List<String> collectClassNames(File root, File current) {
        File[] entries = current.listFiles();
        if (entries == null) return List.of();

        return Arrays.stream(entries)
            .flatMap(file -> {
                if (file.isDirectory()) {
                    return collectClassNames(root, file).stream();
                } else if (file.getName().endsWith(".class")) {
                    return Stream.of(toFQN(root, file));
                }
                return Stream.empty();
            })
            .toList();
    }

    static String toFQN(File root, File classFile) {
        String relative = classFile.getAbsolutePath()
            .substring(root.getAbsolutePath().length() + 1);
        return relative.substring(0, relative.length() - 6)
                       .replace(File.separatorChar, '.');
    }
}
