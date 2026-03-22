package gov.Lab5.command;

import gov.Lab5.repository.Repository;
import gov.Lab5.util.HtmlReportGenerator;

public class ReportCommand implements Command {
    private final Repository repository;
    public ReportCommand(Repository repo) { this.repository = repo; }

    @Override
    public void execute(String[] args) {
        HtmlReportGenerator.generateAndOpen(repository.getAll());
    }

    @Override
    public String getUsage() {
        return "report";
    }
}