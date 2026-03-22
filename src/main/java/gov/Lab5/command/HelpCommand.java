package gov.Lab5.command;

import java.util.Map;

public class HelpCommand implements Command {
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Available commands:");
            commands.keySet().stream().sorted().forEach(name -> {
                Command cmd = commands.get(name);
                System.out.println(name + " - " + cmd.getUsage());
            });
            return;
        }

        String targetName = args[0].toLowerCase();
        Command target = commands.get(targetName);
        if (target == null) {
            System.out.println("Unknown command: " + targetName);
            return;
        }

        System.out.println(targetName + " usage: " + target.getUsage());
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }
}
