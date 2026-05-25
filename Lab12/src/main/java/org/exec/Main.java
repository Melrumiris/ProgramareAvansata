package org.exec;

import org.instrument.BytecodeInstrumentor;
import org.instrument.SourceCompiler;
import org.reflection.AnnotatedMethodRunner;
import org.reflection.ClassFileLoader;
import org.reflection.ClassInspector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: Main <path-to-folder>");
            System.out.println("  The folder may contain .java source files or .class bytecode.");
            return;
        }

        File inputFolder = new File(args[0]);
        if (!inputFolder.isDirectory()) {
            System.err.println("[error] Not a valid directory: " + inputFolder.getAbsolutePath());
            return;
        }

        File classesDir = prepareClassesDir(inputFolder);
        if (classesDir == null) {
            System.err.println("[error] No .java or .class files found in: " + inputFolder);
            return;
        }

        System.out.println("\n=== Loading classes from: " + classesDir.getAbsolutePath() + " ===\n");

        List<Class<?>> allClasses     = ClassFileLoader.loadClasses(classesDir);
        List<Class<?>> annotationTypes = ClassInspector.findAnnotationTypes(allClasses);
        List<Class<?>> publicClasses   = ClassInspector.findPublicClasses(allClasses);

        System.out.println("Loaded " + allClasses.size() + " class(es).");
        System.out.println("Annotation types: "
            + annotationTypes.stream().map(c -> "@" + c.getSimpleName()).toList());

        System.out.println("\n=== Class Prototypes ===\n");
        publicClasses.forEach(ClassInspector::printPrototype);

        List<String> annotationFQNs = annotationTypes.stream()
                                                      .map(Class::getName)
                                                      .toList();

        System.out.println("\n=== Bytecode Instrumentation ===\n");
        BytecodeInstrumentor.instrument(classesDir, annotationFQNs);

        System.out.println("\n=== Invoking Annotated Methods (instrumented) ===\n");
        List<Class<?>> reloaded            = ClassFileLoader.loadClasses(classesDir);
        List<Class<?>> reloadedAnnotations = ClassInspector.findAnnotationTypes(reloaded);
        List<Class<?>> reloadedPublic      = ClassInspector.findPublicClasses(reloaded);

        AnnotatedMethodRunner.runAnnotatedMethods(reloadedPublic, reloadedAnnotations);
    }

    private static File prepareClassesDir(File inputFolder) throws IOException {
        boolean hasJava  = containsFileType(inputFolder, ".java");
        boolean hasClass = containsFileType(inputFolder, ".class");

        if (hasJava) {
            File tempDir = Files.createTempDirectory("lab12_compiled_").toFile();
            tempDir.deleteOnExit();
            System.out.println("=== Detected .java source files — compiling... ===");
            return SourceCompiler.compile(inputFolder, tempDir);
        } else if (hasClass) {
            System.out.println("=== Detected .class files — skipping compilation. ===");
            return inputFolder;
        }

        return null;
    }

    private static boolean containsFileType(File folder, String extension) throws IOException {
        try (var paths = Files.walk(folder.toPath())) {
            return paths.anyMatch(p -> p.toString().endsWith(extension));
        }
    }
}
