package org.instrument;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BytecodeInstrumentor {

    public static void instrument(File classesDir, List<String> annotationFQNs) throws Exception {
        ClassPool pool = new ClassPool(true);
        pool.insertClassPath(classesDir.getAbsolutePath());

        for (String className : collectClassNames(classesDir, classesDir)) {
            CtClass ctClass = pool.get(className);

            if (ctClass.isAnnotation() || ctClass.isInterface()) {
                ctClass.detach();
                continue;
            }

            boolean modified = instrumentMethods(ctClass, annotationFQNs, classesDir);
            if (!modified) {
                ctClass.detach();
            }
        }
    }

    private static boolean instrumentMethods(CtClass ctClass, List<String> annotationFQNs,
                                             File classesDir) throws Exception {
        boolean modified = false;

        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (hasTargetAnnotation(method, annotationFQNs)) {
                String logSnippet = "System.out.println(\"[LOG] Entering "
                    + ctClass.getSimpleName() + "." + method.getName() + "\");";
                method.insertBefore(logSnippet);
                System.out.println("  [instrument] "
                    + ctClass.getSimpleName() + "." + method.getName());
                modified = true;
            }
        }

        if (modified) {
            ctClass.writeFile(classesDir.getAbsolutePath());
            ctClass.detach();
        }

        return modified;
    }

    // Checks whether the method carries at least one runtime-visible annotation
    // whose FQN appears in the annotationFQNs list.
    // Uses AnnotationsAttribute for bytecode-level inspection to avoid any
    // cross-ClassLoader Class<?> identity issues.
    private static boolean hasTargetAnnotation(CtMethod method, List<String> annotationFQNs) {
        MethodInfo info = method.getMethodInfo();
        AnnotationsAttribute attr = (AnnotationsAttribute)
            info.getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) return false;

        return Arrays.stream(attr.getAnnotations())
                     .map(Annotation::getTypeName)
                     .anyMatch(annotationFQNs::contains);
    }

    // Recursively collects FQN class names from .class files under root.
    // Same logic as ClassFileLoader — duplicated to avoid a cross-package dependency
    // on a package-private helper.
    private static List<String> collectClassNames(File root, File current) {
        File[] entries = current.listFiles();
        if (entries == null) return List.of();

        return Arrays.stream(entries)
            .flatMap(f -> f.isDirectory()
                ? collectClassNames(root, f).stream()
                : f.getName().endsWith(".class")
                    ? Stream.of(toFQN(root, f))
                    : Stream.empty())
            .toList();
    }

    private static String toFQN(File root, File classFile) {
        String rel = classFile.getAbsolutePath()
            .substring(root.getAbsolutePath().length() + 1);
        return rel.substring(0, rel.length() - 6).replace(File.separatorChar, '.');
    }
}
