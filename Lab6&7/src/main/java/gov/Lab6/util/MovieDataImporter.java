package gov.Lab6.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvValidationException;
import gov.Lab6.conn.DBConnManager;
import gov.Lab6.dao.ActorDAO;
import gov.Lab6.dao.CharacterDAO;
import gov.Lab6.dao.GenreDAO;
import gov.Lab6.dao.MovieDAO;
import gov.Lab6.data.ActorData;
import gov.Lab6.data.GenreData;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.builder.ActorBuilder;
import gov.Lab6.data.builder.CharacterBuilder;
import gov.Lab6.data.builder.GenreBuilder;
import gov.Lab6.data.builder.MovieBuilder;
import gov.Lab6.exception.IllegalDataException;
import gov.Lab6.exception.NullDataException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDataImporter {

    private static final int MAX_CAST_PER_MOVIE = 5;

    private final MovieDAO movieDAO    = new MovieDAO();
    private final ActorDAO actorDAO    = new ActorDAO();
    private final GenreDAO genreDAO    = new GenreDAO();
    private final CharacterDAO charDAO = new CharacterDAO();
    private final ObjectMapper json    = new ObjectMapper();

    public void importFrom(String moviesMetadataPath, String creditsPath) {
        checkDbConnection();

        System.out.println("Parsing movies_metadata.csv …");
        Map<String, MovieRecord> movieRecords = parseMoviesMetadata(moviesMetadataPath);
        System.out.printf("  Parsed %d valid movie records.%n", movieRecords.size());

        System.out.println("Inserting movies and cast …");
        parseCreditAndInsert(creditsPath, movieRecords);

        System.out.println("Import complete.");
    }

    private void checkDbConnection() {
        try {
            var conn = DBConnManager.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                throw new RuntimeException("Database connection is not available.");
            }
            conn.prepareStatement("SELECT 1").executeQuery();
            System.out.println("Database connection verified.");
        } catch (SQLException e) {
            throw new RuntimeException("Database is not reachable: " + e.getMessage(), e);
        }
    }

    // Parse CSVs

    private Map<String, MovieRecord> parseMoviesMetadata(String path) {
        Map<String, MovieRecord> records = new HashMap<>();
        try (CSVReader reader = buildCsvReader(path)) {
            String[] header = reader.readNext();
            if (header == null) return records;

            Map<String, Integer> idx = buildIndex(header);
            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    MovieRecord rec = parseMovieRow(row, idx);
                    if (rec != null) records.put(rec.csvId, rec);
                } catch (Exception e) {
                    System.err.printf("  Skipping movie row: %s%n", e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to read movies_metadata.csv: " + e.getMessage(), e);
        }
        return records;
    }

    private MovieRecord parseMovieRow(String[] row, Map<String, Integer> idx) throws IllegalDataException {
        String csvId = cell(row, idx, "id");
        String title = cell(row, idx, "title");
        if (csvId == null || csvId.isBlank() || title == null || title.isBlank()) return null;
        if (!csvId.matches("\\d+")) return null;

        String genreName = extractFirstGenreName(cell(row, idx, "genres"));
        if (genreName == null) genreName = "Unknown";

        Date releaseDate = parseDate(cell(row, idx, "release_date"));

        double voteAvg = 0;
        try {
            String raw = cell(row, idx, "vote_average");
            if (raw != null && !raw.isBlank()) voteAvg = Double.parseDouble(raw);
        } catch (NumberFormatException ignored) {}
        int score = (int) Math.max(0, Math.min(10, Math.round(voteAvg)));

        return new MovieRecord(csvId, title, releaseDate, score, genreName);
    }

    private String extractFirstGenreName(String genresJson) {
        if (genresJson == null || genresJson.isBlank() || genresJson.equals("[]")) return null;
        try {
            List<Map<String, Object>> genres = json.readValue(normalisePythonRepr(genresJson), new TypeReference<>() {});
            if (!genres.isEmpty()) {
                Object name = genres.get(0).get("name");
                if (name instanceof String s && !s.isBlank()) return s;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void parseCreditAndInsert(String path, Map<String, MovieRecord> movieRecords) {
        int inserted = 0;
        int skipped  = 0;
        try (CSVReader reader = buildCsvReader(path)) {
            String[] header = reader.readNext();
            if (header == null) return;

            Map<String, Integer> idx = buildIndex(header);
            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    String csvId = cell(row, idx, "id");
                    MovieRecord rec = movieRecords.get(csvId);
                    if (rec == null) { skipped++; continue; }

                    MovieData movie = insertMovie(rec);
                    if (movie == null) { skipped++; continue; }

                    for (CastEntry entry : parseCast(cell(row, idx, "cast"))) {
                        insertCharacter(entry, movie);
                    }
                    inserted++;
                } catch (Exception e) {
                    skipped++;
                    System.err.printf("  Skipping credits row: %s%n", e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to read credits.csv: " + e.getMessage(), e);
        }
        System.out.printf("  Inserted %d movies, skipped %d rows.%n", inserted, skipped);
    }

    private MovieData insertMovie(MovieRecord rec) {
        List<MovieData> existing = movieDAO.getByName(rec.title);
        if (!existing.isEmpty()) return existing.get(0);

        GenreData genre = findOrCreateGenre(rec.genreName);
        if (genre == null) return null;

        MovieBuilder mb = new MovieBuilder()
                .setTitle(rec.title)
                .setReleaseDate(rec.releaseDate)
                .setGenre(genre);
        try { mb.setScore(rec.score); } catch (IllegalDataException ignored) {}

        movieDAO.add(mb);
        try {
            return mb.build();
        } catch (NullDataException e) {
            System.err.println("Failed to retrieve inserted movie ID: " + e.getMessage());
            return null;
        }
    }

    private GenreData findOrCreateGenre(String name) {
        var existing = genreDAO.getByName(name);
        if (existing.isPresent()) return existing.get();

        GenreBuilder gb = new GenreBuilder().setName(name);
        genreDAO.add(gb);
        try {
            return gb.build();
        } catch (NullDataException e) {
            System.err.println("Failed to retrieve inserted genre ID: " + e.getMessage());
            return null;
        }
    }

    private void insertCharacter(CastEntry entry, MovieData movie) {
        List<ActorData> existing = actorDAO.getByName(entry.actorName);
        ActorData actor;
        if (!existing.isEmpty()) {
            actor = existing.get(0);
        } else {
            ActorBuilder ab = new ActorBuilder().setName(entry.actorName);
            actorDAO.add(ab);
            try {
                actor = ab.build();
            } catch (NullDataException e) {
                System.err.println("Failed to retrieve inserted actor ID: " + e.getMessage());
                return;
            }
        }

        CharacterBuilder cb = new CharacterBuilder()
                .setName(entry.characterName.isBlank() ? entry.actorName : entry.characterName)
                .setActor(actor)
                .setMovie(movie);
        charDAO.add(cb);
    }

    private List<CastEntry> parseCast(String castRaw) {
        if (castRaw == null || castRaw.isBlank() || castRaw.equals("[]")) return List.of();
        try {
            List<Map<String, Object>> castList = json.readValue(normalisePythonRepr(castRaw), new TypeReference<>() {});
            List<CastEntry> result = new ArrayList<>();
            for (Map<String, Object> member : castList) {
                if (result.size() >= MAX_CAST_PER_MOVIE) break;
                String name      = (String) member.getOrDefault("name", "");
                String character = (String) member.getOrDefault("character", "");
                if (name != null && !name.isBlank()) {
                    result.add(new CastEntry(name.trim(), character == null ? "" : character.trim()));
                }
            }
            return result;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private String normalisePythonRepr(String raw) {
        StringBuilder out = new StringBuilder(raw.length());
        boolean inString = false;
        char delim = 0;
        int i = 0;

        while (i < raw.length()) {
            char c = raw.charAt(i);

            if (!inString) {
                if (c == '\'' || c == '"') {
                    inString = true;
                    delim = c;
                    out.append('"');
                    i++;
                } else if (c == 'N' && raw.startsWith("None", i) && !isAlphanumericAt(raw, i + 4)) {
                    out.append("null");
                    i += 4;
                } else if (c == 'T' && raw.startsWith("True", i) && !isAlphanumericAt(raw, i + 4)) {
                    out.append("true");
                    i += 4;
                } else if (c == 'F' && raw.startsWith("False", i) && !isAlphanumericAt(raw, i + 5)) {
                    out.append("false");
                    i += 5;
                } else {
                    out.append(c);
                    i++;
                }
            } else {
                if (c == '\\' && i + 1 < raw.length()) {
                    char next = raw.charAt(i + 1);
                    if (next == delim) {
                        // Escaped delimiter inside string: single-quoted → plain apostrophe in JSON
                        out.append(delim == '\'' ? "'" : "\\\"");
                        i += 2;
                    } else if (next == '\\' || next == 'n' || next == 't' || next == 'r') {
                        out.append('\\');
                        out.append(next);
                        i += 2;
                    } else {
                        out.append(c);
                        i++;
                    }
                } else if (c == delim) {
                    inString = false;
                    out.append('"');
                    i++;
                } else if (c == '"' && delim == '\'') {
                    // Unescaped double-quote inside a single-quoted string must be escaped for JSON
                    out.append("\\\"");
                    i++;
                } else {
                    out.append(c);
                    i++;
                }
            }
        }
        return out.toString();
    }

    private boolean isAlphanumericAt(String s, int i) {
        return i < s.length() && Character.isLetterOrDigit(s.charAt(i));
    }

    // Helpers
    
    private CSVReader buildCsvReader(String path) throws IOException {
        return new CSVReaderBuilder(new FileReader(path, StandardCharsets.UTF_8))
                .withCSVParser(new RFC4180ParserBuilder().withSeparator(',').build())
                .build();
    }

    private Date parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return Date.valueOf(raw); } catch (IllegalArgumentException ignored) { return null; }
    }

    private String cell(String[] row, Map<String, Integer> idx, String column) {
        Integer i = idx.get(column);
        if (i == null || i >= row.length) return null;
        return row[i].trim();
    }

    private Map<String, Integer> buildIndex(String[] header) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++) idx.put(header[i].trim(), i);
        return idx;
    }

    private record MovieRecord(String csvId, String title, Date releaseDate, int score, String genreName) {}
    private record CastEntry(String actorName, String characterName) {}
}