package gov.Lab6.dao;

import gov.Lab6.conn.DBConnManager;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.builder.DataBuilder;
import gov.Lab6.data.builder.GenreBuilder;
import gov.Lab6.data.builder.MovieBuilder;
import gov.Lab6.exception.IllegalDataException;
import gov.Lab6.exception.NullDataException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieDAO implements DAO<MovieData> {

    @Override
    public Optional<MovieData> get(int id) {
        Connection manager = DBConnManager.getInstance().getConnection();
        MovieBuilder builder = new MovieBuilder();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM movies JOIN genres ON movies.genre_id = genres.id WHERE movies.id = ?");
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setTitle(rs.getString("title"));
                builder.setReleaseDate(rs.getDate("release_date"));
                builder.setDuration(rs.getTime("duration"));
                builder.setScore(rs.getInt("score"));
                builder.setGenre(new GenreBuilder().setID(rs.getInt("genre_id")).setName(rs.getString("name")).build());
                return Optional.of(builder.build());
            }
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<MovieData> getAll() {
        Connection manager = DBConnManager.getInstance().getConnection();
        MovieBuilder builder = new MovieBuilder();
        List<MovieData> result = new ArrayList<>();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM movies JOIN genres ON movies.genre_id = genres.id");
            var rs = stmt.executeQuery();
            while (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setTitle(rs.getString("title"));
                builder.setReleaseDate(rs.getDate("release_date"));
                builder.setDuration(rs.getTime("duration"));
                builder.setScore(rs.getInt("score"));
                builder.setGenre(new GenreBuilder().setID(rs.getInt("genre_id")).setName(rs.getString("name")).build());
                result.add(builder.build());
            }
            return result;
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<MovieData> getByName(String name) {
        Connection manager = DBConnManager.getInstance().getConnection();
        MovieBuilder builder = new MovieBuilder();
        List<MovieData> result = new ArrayList<>();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM movies JOIN genres ON movies.genre_id = genres.id WHERE title = ?");
            stmt.setString(1, name);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setTitle(rs.getString("title"));
                builder.setReleaseDate(rs.getDate("release_date"));
                builder.setDuration(rs.getTime("duration"));
                builder.setScore(rs.getInt("score"));
                builder.setGenre(new GenreBuilder().setID(rs.getInt("genre_id")).setName(rs.getString("name")).build());
                result.add(builder.build());
            }
            return result;
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<MovieData> getFromReport() {
        Connection manager = DBConnManager.getInstance().getConnection();
        List<MovieData> result = new ArrayList<>();
        try {
            var stmt = manager.prepareStatement("SELECT id, title, release_date, duration, score, genre_id, genre_name FROM movie_report_view ORDER BY title");
            var rs = stmt.executeQuery();
            while (rs.next()) {
                MovieBuilder builder = new MovieBuilder();
                builder.setID(rs.getInt("id"));
                builder.setTitle(rs.getString("title"));
                builder.setReleaseDate(rs.getDate("release_date"));
                builder.setDuration(rs.getTime("duration"));
                builder.setScore(rs.getInt("score"));
                builder.setGenre(new GenreBuilder().setID(rs.getInt("genre_id")).setName(rs.getString("genre_name")).build());
                result.add(builder.build());
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute report view query:" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalDataException e) {
            System.err.println("Failed to build movie data object:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public DAO<MovieData> remove(MovieData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("DELETE FROM movies WHERE id = ?");
            stmt.setInt(1, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<MovieData> update(MovieData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("UPDATE movies SET title = ?, release_date = ?, duration = ?, score = ?, genre_id = ? WHERE id = ?");
            stmt.setString(1, item.getTitle());
            stmt.setDate(2, item.getReleaseDate());
            stmt.setTime(3, item.getDuration());
            stmt.setInt(4, item.getScore());
            stmt.setInt(5, item.getGenre().getId());
            stmt.setInt(6, item.getId());
            stmt.executeUpdate();
                    }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<MovieData> add(DataBuilder<MovieData> builder) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var seqRs = manager.prepareStatement("SELECT NEXTVAL('movies_id_seq')").executeQuery();
            seqRs.next();
            builder.setID(seqRs.getInt(1));
            MovieData data = builder.build();
            var stmt = manager.prepareStatement("INSERT INTO movies (id, title, release_date, duration, score, genre_id) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, data.getId());
            stmt.setString(2, data.getTitle());
            stmt.setDate(3, data.getReleaseDate());
            stmt.setTime(4, data.getDuration());
            stmt.setInt(5, data.getScore());
            stmt.setInt(6, data.getGenre().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }
}
