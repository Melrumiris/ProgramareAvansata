package gov.Lab5.main;

import gov.Lab5.command.*;
import gov.Lab5.exception.InvalidResourceException;
import gov.Lab5.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Repository repo = new Repository("src/main/resources/resources.json");
        Map<String, Command> commands = new HashMap<>();
        commands.put("add", new AddCommand(repo));
        commands.put("list", new ListCommand(repo));
        commands.put("view", new ViewCommand(repo));
        commands.put("report", new ReportCommand(repo));
        commands.put("test", new TestPerformanceCommand());
        commands.put("help", new HelpCommand(commands));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println(">> Exiting...");
                break;
            }

            String[] parts = input.split(" ");
            String cmdName = parts[0].toLowerCase();
            String[] cmdArgs = Arrays.copyOfRange(parts, 1, parts.length);

            Command command = commands.get(cmdName);
            if (command != null) {
                try {
                    command.execute(cmdArgs);
                } catch (InvalidResourceException e) {
                    System.err.println(">> Operation Failed: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println(">> Critical Error: " + e.getMessage());
                }
            } else if (!cmdName.isEmpty()) {
                System.out.println(">> Unknown protocol. Available: " + String.join(", ", commands.keySet()) + ", exit");
            }
        }
    }
}