package gov.Lab6.dao;

import gov.Lab6.conn.DBConnManager;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.MovieListData;
import gov.Lab6.data.builder.DataBuilder;
import gov.Lab6.data.builder.MovieListBuilder;
import gov.Lab6.exception.NullDataException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovieListDAO implements DAO<MovieListData> {

    private final MovieDAO movieDAO = new MovieDAO();

    @Override
    public Optional<MovieListData> get(int id) {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var stmt = conn.prepareStatement(
                    "SELECT ml.id, ml.name, ml.created_at, m.id AS movie_id " +
                    "FROM movie_lists ml " +
                    "LEFT JOIN movie_list_movies mlm ON ml.id = mlm.list_id " +
                    "LEFT JOIN movies m ON mlm.movie_id = m.id " +
                    "WHERE ml.id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            MovieListBuilder builder = null;
            while (rs.next()) {
                if (builder == null) {
                    builder = new MovieListBuilder()
                            .setID(rs.getInt("id"))
                            .setName(rs.getString("name"))
                            .setCreatedAt(rs.getTimestamp("created_at"));
                }
                int movieId = rs.getInt("movie_id");
                if (!rs.wasNull()) {
                    movieDAO.get(movieId).ifPresent(builder::addMovie);
                }
            }
            if (builder != null) {
                return Optional.of(builder.build());
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<MovieListData> getAll() {
        Connection conn = DBConnManager.getInstance().getConnection();
        List<MovieListData> result = new ArrayList<>();
        try {
            var stmt = conn.prepareStatement(
                    "SELECT ml.id, ml.name, ml.created_at, mlm.movie_id " +
                    "FROM movie_lists ml " +
                    "LEFT JOIN movie_list_movies mlm ON ml.id = mlm.list_id " +
                    "ORDER BY ml.id");
            ResultSet rs = stmt.executeQuery();

            Map<Integer, MovieListBuilder> builders = new LinkedHashMap<>();
            while (rs.next()) {
                int listId = rs.getInt("id");
                MovieListBuilder builder = builders.computeIfAbsent(listId, k -> {
                    try {
                        return new MovieListBuilder()
                                .setID(listId)
                                .setName(rs.getString("name"))
                                .setCreatedAt(rs.getTimestamp("created_at"));
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                int movieId = rs.getInt("movie_id");
                if (!rs.wasNull()) {
                    movieDAO.get(movieId).ifPresent(builder::addMovie);
                }
            }
            for (MovieListBuilder builder : builders.values()) {
                result.add(builder.build());
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public DAO<MovieListData> add(DataBuilder<MovieListData> builder) {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var insert = conn.prepareStatement(
                    "INSERT INTO movie_lists (name, created_at) VALUES (?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS);

            MovieListBuilder listBuilder = (MovieListBuilder) builder;
            insert.setString(1, listBuilder.getName());
            insert.setTimestamp(2, listBuilder.getCreatedAt());
            insert.executeUpdate();

            ResultSet keys = insert.getGeneratedKeys();
            if (keys.next()) {
                int generatedId = keys.getInt(1);
                builder.setID(generatedId);
                insertMovieLinks(conn, generatedId, listBuilder.getMovies());
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<MovieListData> update(MovieListData item) {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var update = conn.prepareStatement(
                    "UPDATE movie_lists SET name = ?, created_at = ? WHERE id = ?");
            update.setString(1, item.getName());
            update.setTimestamp(2, item.getCreatedAt());
            update.setInt(3, item.getId());
            update.executeUpdate();

            var deleteLinks = conn.prepareStatement(
                    "DELETE FROM movie_list_movies WHERE list_id = ?");
            deleteLinks.setInt(1, item.getId());
            deleteLinks.executeUpdate();

            insertMovieLinks(conn, item.getId(), item.getMovies());
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<MovieListData> remove(MovieListData item) {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var stmt = conn.prepareStatement("DELETE FROM movie_lists WHERE id = ?");
            stmt.setInt(1, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    private void insertMovieLinks(Connection conn, int listId, List<MovieData> movies) throws SQLException {
        if (movies.isEmpty()) return;
        var linkInsert = conn.prepareStatement(
                "INSERT INTO movie_list_movies (list_id, movie_id) VALUES (?, ?)");
        for (MovieData movie : movies) {
            linkInsert.setInt(1, listId);
            linkInsert.setInt(2, movie.getId());
            linkInsert.addBatch();
        }
        linkInsert.executeBatch();
    }
}
