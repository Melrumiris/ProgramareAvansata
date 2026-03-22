package gov.Lab5.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import gov.Lab5.model.Resource;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlReportGenerator {
    public static void generateAndOpen(List<Resource> resources) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

            cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Map<String, Object> root = new HashMap<>();
            root.put("resources", resources);

            Template temp = cfg.getTemplate("report.ftl");
            File outputFile = new File("report.html");

            try (Writer out = new FileWriter(outputFile)) {
                temp.process(root, out);
            }

            Desktop.getDesktop().browse(outputFile.toURI());

        } catch (Exception e) {
            System.err.println("Report generation failure: " + e.getMessage());
        }
    }
}