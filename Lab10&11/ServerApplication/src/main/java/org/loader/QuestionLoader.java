package org.loader;

import org.database.dao.QuestionRepository;
import org.database.entity.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionLoader {
    private static final Pattern ANSWER_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: QuestionLoader <path-to-questions-file>");
            System.exit(1);
        }

        String filePath = args[0];
        QuestionRepository repository = new QuestionRepository();
        int loaded = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || !line.startsWith("\\q ")) {
                    if (!line.isEmpty()) {
                        System.out.println("[Skip] Line " + lineNumber + ": does not start with '\\q '");
                        skipped++;
                    }
                    continue;
                }

                // Strip the '\q ' prefix
                String content = line.substring(3).trim();

                // Extract correct answers: all [answer] groups
                List<String> correctAnswers = new ArrayList<>();
                Matcher matcher = ANSWER_PATTERN.matcher(content);
                while (matcher.find()) {
                    correctAnswers.add(matcher.group(1).trim());
                }

                if (correctAnswers.isEmpty()) {
                    System.out.println("[Skip] Line " + lineNumber + ": no correct answers found (use [answer] format)");
                    skipped++;
                    continue;
                }

                // Question text is everything before the first '['
                int firstBracket = content.indexOf('[');
                String questionText = content.substring(0, firstBracket).trim();

                if (questionText.isEmpty()) {
                    System.out.println("[Skip] Line " + lineNumber + ": empty question text");
                    skipped++;
                    continue;
                }

                Question question = new Question(questionText, correctAnswers);
                repository.create(question);
                System.out.println("[OK] Loaded: \"" + questionText + "\" -> " + correctAnswers);
                loaded++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\nDone. Loaded: " + loaded + ", Skipped: " + skipped);
    }
}
