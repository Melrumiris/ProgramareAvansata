package org.commands;

import org.connections.ClientThread;

import java.util.Map;
import java.util.function.BiFunction;

public class CommandFactory {

    /**
     * Dispatch table mapping each command token to a factory function.
     * Commands that take no argument string use a lambda that ignores the second parameter.
     */
    private static final Map<String, BiFunction<ClientThread, String, Command>> COMMANDS =
            Map.ofEntries(
                    Map.entry("\\login",     LoginCommand::new),
                    Map.entry("\\register",  RegisterCommand::new),
                    Map.entry("\\logout",    (client, args) -> new LogoutCommand(client)),
                    Map.entry("\\opengame",  OpenGameCommand::new),
                    Map.entry("\\joingame",  JoinGameCommand::new),
                    Map.entry("\\startgame", StartGameCommand::new),
                    Map.entry("\\answer",    AnswerCommand::new),
                    Map.entry("\\search",       SearchCommand::new),
                    Map.entry("\\stop",         (client, args) -> new StopCommand(client)),
                    Map.entry("\\help",         (client, args) -> new HelpCommand(client)),
                    Map.entry("\\getvocabulary",(client, args) -> new GetVocabularyCommand(client)),
                    Map.entry("\\addbot",       AddBotCommand::new)
            );

    public static Command createCommand(String cmd, ClientThread client) {
        if (cmd == null || cmd.isEmpty())
            return new InvalidCommand(client, "Empty command");
        if (cmd.charAt(0) != '\\')
            return new InvalidCommand(client, "Commands must start with '\\'. Try \\help for a list of commands.");

        String[] parts = cmd.split(" ", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length == 2 ? parts[1] : "";

        BiFunction<ClientThread, String, Command> factory = COMMANDS.get(command);
        if (factory != null) {
            return factory.apply(client, args);
        }
        return new InvalidCommand(client, "Unknown command: " + command + ". Try \\help for a list of commands.");
    }
}
