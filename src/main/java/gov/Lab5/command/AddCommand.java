package gov.Lab5.command;


import gov.Lab5.exception.InvalidResourceException;
import gov.Lab5.model.Resource;
import gov.Lab5.repository.Repository;

import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AddCommand implements Command {
    private final Repository repository;
    public AddCommand(Repository repo) { this.repository = repo; }

    @Override
    public void execute(String[] args) throws InvalidResourceException {
        if (args.length < 5) {
            System.out.println("Usage: add <id> <title> <location> <year> <authors> [concept1,concept2...]");
            return;
        }
        Set<String> concepts = args.length > 5 ? Arrays.stream(args[5].split(",")).collect(Collectors.toSet()) : Set.of();
        Resource res = new Resource(args[0], args[1], args[2], Integer.parseInt(args[3]), args[4], "", concepts);
        repository.add(res);
    }

    @Override
    public String getUsage() {
        return "add <id> <title> <location> <year> <authors> [concept1,concept2...]";
    }
}