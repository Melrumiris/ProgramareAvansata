package org.commands;

import org.connections.ClientThread;

public class CommandFactory {
    public static Command createCommand(String cmd, ClientThread client) {
        if (cmd == null || cmd.isEmpty())
            return new InvalidCommand(client, "Empty command");
        if (cmd.charAt(0) != '\\')
            return new InvalidCommand(client, "Commands must start with '\\'. Try \\help for a list of commands.");

        String[] parts = cmd.split(" ", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length == 2 ? parts[1] : "";

        switch (command) {
            case "\\login":      return new LoginCommand(client, args);
            case "\\register":   return new RegisterCommand(client, args);
            case "\\logout":     return new LogoutCommand(client);
            case "\\opengame":  return new OpenGameCommand(client, args);
            case "\\joingame":  return new JoinGameCommand(client, args);
            case "\\startgame": return new StartGameCommand(client, args);
            case "\\answer":    return new AnswerCommand(client, args);
            case "\\stop":      return new StopCommand(client);
            case "\\help":      return new HelpCommand(client);
            default:            return new InvalidCommand(client, "Unknown command: " + command + ". Try \\help for a list of commands.");
        }
    }
}
