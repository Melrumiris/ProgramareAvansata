package gov.Lab5.command;

import gov.Lab5.exception.InvalidResourceException;

public interface Command {
    void execute(String[] args) throws InvalidResourceException;

    String getUsage();
}