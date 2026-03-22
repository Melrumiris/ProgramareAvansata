package gov.Lab5.command;

import gov.Lab5.model.Resource;
import gov.Lab5.repository.Repository;

import java.awt.Desktop;
import java.net.URI;
import java.io.File;

public class ViewCommand implements Command {
    private final Repository repository;
    public ViewCommand(Repository repo) { this.repository = repo; }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: view <id>");
            return;
        }
        Resource res = repository.getAll().stream().filter(r -> r.id().equals(args[0])).findFirst().orElse(null);
        if (res == null) {
            System.out.println("Error: Resource not found.");
            return;
        }
        try {
            Desktop desktop = Desktop.getDesktop();
            if (res.location().startsWith("http")) desktop.browse(new URI(res.location()));
            else desktop.open(new File(res.location()));
            System.out.println("Launching: " + res.title());
        } catch (Exception e) {
            System.err.println("Launch failed: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "view <id>";
    }
}