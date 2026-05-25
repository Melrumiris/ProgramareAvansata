package org.app;

import org.com.DisplayLocales;
import org.com.Info;
import org.com.SetLocale;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LocaleExplore {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ResourceBundle messages = ResourceBundle.getBundle("Message", Locale.getDefault());

        while (true) {
            System.out.print(messages.getString("prompt") + " ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "displaylocales":
                    new DisplayLocales().execute(messages);
                    break;

                case "setlocale":
                    if (tokens.length > 1) {
                        new SetLocale().execute(tokens[1], messages);
                        messages = ResourceBundle.getBundle("Message", Locale.getDefault());
                    } else {
                        System.out.println("Please specify a locale (e.g., setlocale ro-RO)");
                    }
                    break;

                case "info":
                    String tag = tokens.length > 1 ? tokens[1] : null;
                    new Info().execute(tag, messages);
                    break;

                default:
                    System.out.println(messages.getString("invalid"));
                    break;
            }
        }
        scanner.close();
    }
}