package gov.Lab6.dao;

import gov.Lab6.conn.DBConnManager;
import gov.Lab6.data.GenreData;
import gov.Lab6.data.builder.DataBuilder;
import gov.Lab6.data.builder.GenreBuilder;
import gov.Lab6.exception.NullDataException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class GenreDAO implements DAO<GenreData> {

    @Override
    public Optional<GenreData> get(int id) {
        Connection manager = DBConnManager.getInstance().getConnection();
        GenreBuilder builder = new GenreBuilder();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM genres WHERE id = ?");
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setName(rs.getString("name"));
                return Optional.of(builder.build());
            }
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<GenreData> getByName(String name) {
        Connection manager = DBConnManager.getInstance().getConnection();
        GenreBuilder builder = new GenreBuilder();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM genres WHERE name = ?");
            stmt.setString(1, name);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setName(rs.getString("name"));
                return Optional.of(builder.build());
            }
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public GenreDAO remove(GenreData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("DELETE FROM genres WHERE id = ?");
            stmt.setInt(1, item.getId());
            stmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public GenreDAO update(GenreData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("UPDATE genres SET name = ? WHERE id = ?");
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getId());
            stmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public GenreDAO add(DataBuilder<GenreData> builder) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var seqRs = manager.prepareStatement("SELECT NEXTVAL('genres_id_seq')").executeQuery();
            seqRs.next();
            builder.setID(seqRs.getInt(1));
            GenreData data = builder.build();
            var stmt = manager.prepareStatement("INSERT INTO genres (id, name) VALUES (?, ?)");
            stmt.setInt(1, data.getId());
            stmt.setString(2, data.getName());
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
