package gov.Lab5.command;

public interface Command {
    void execute(String[] args);

    String getUsage();
}