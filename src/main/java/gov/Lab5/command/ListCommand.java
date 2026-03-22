package gov.Lab5.command;

import gov.Lab5.repository.Repository;

public class ListCommand implements Command {
    private final Repository repository;
    public ListCommand(Repository repo) { this.repository = repo; }

    @Override
    public void execute(String[] args) {
        repository.getAll().forEach(r -> System.out.println(r.id() + " | " + r.title() + " (" + r.year() + ") - " + r.concepts()));
    }

    @Override
    public String getUsage() {
        return "list";
    }
}