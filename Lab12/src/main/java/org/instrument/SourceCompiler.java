package org.instrument;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SourceCompiler {

   public static File compile(File sourceFolder, File outputDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                "No system Java compiler available — make sure you are running on a JDK.");
        }

        outputDir.mkdirs();

        List<String> javaFilePaths = collectJavaFiles(sourceFolder).stream()
            .map(File::getAbsolutePath)
            .toList();

        if (javaFilePaths.isEmpty()) {
            System.out.println("  [info] No .java files found in: " + sourceFolder);
            return outputDir;
        }

        System.out.println("  Compiling " + javaFilePaths.size() + " file(s) → " + outputDir);

        // Build the argument list: javac -d <outputDir> <file1> <file2> ...
        List<String> args = new ArrayList<>(List.of("-d", outputDir.getAbsolutePath()));
        args.addAll(javaFilePaths);

        int exitCode = compiler.run(null, null, null, args.toArray(new String[0]));
        if (exitCode != 0) {
            throw new RuntimeException("Compilation failed (javac exit code " + exitCode + ")");
        }

        System.out.println("  Compilation successful.");
        return outputDir;
    }

   private static List<File> collectJavaFiles(File folder) {
        File[] entries = folder.listFiles();
        if (entries == null) return List.of();

        return Arrays.stream(entries)
            .flatMap(f -> {
                if (f.isDirectory()) return collectJavaFiles(f).stream();
                if (f.getName().endsWith(".java")) return Stream.of(f);
                return Stream.empty();
            })
            .toList();
    }
}
