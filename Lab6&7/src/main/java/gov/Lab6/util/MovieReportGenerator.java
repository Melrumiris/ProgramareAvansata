package gov.Lab6.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import gov.Lab6.dao.MovieDAO;
import gov.Lab6.data.MovieData;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MovieReportGenerator {

    private MovieReportGenerator() {
    }

    public static File generate(List<MovieData> movies) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setClassLoaderForTemplateLoading(
                    Thread.currentThread().getContextClassLoader(), "templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Map<String, Object> root = new HashMap<>();
            root.put("movies", movies);

            Template template = cfg.getTemplate("movies-report.ftl");
            File outputFile = new File("movies-report.html");

            try (Writer out = new FileWriter(outputFile)) {
                template.process(root, out);
            }
            return outputFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate movies report", e);
        }
    }

    public static File generateFromView() {
        MovieDAO dao = new MovieDAO();
        return generate(dao.getFromReport());
    }

    public static void generateAndOpenFromView() {
        File target = generateFromView();
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(target.toURI());
            }
        } catch (Exception e) {
            throw new RuntimeException("Report generated but could not be opened", e);
        }
    }
}

