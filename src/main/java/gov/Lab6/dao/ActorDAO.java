package gov.Lab6.dao;

import gov.Lab6.conn.DBConnManager;
import gov.Lab6.data.ActorData;
import gov.Lab6.data.GenreData;
import gov.Lab6.data.builder.ActorBuilder;
import gov.Lab6.data.builder.DataBuilder;
import gov.Lab6.data.builder.GenreBuilder;
import gov.Lab6.exceptions.NullDataException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ActorDAO implements DAO<ActorData> {

    @Override
    public Optional<ActorData> get(int id) {
        Connection manager = DBConnManager.getInstance().getConnection();
        ActorBuilder builder = new ActorBuilder();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM actors WHERE id = ?");
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

    @Override
    public DAO<ActorData> remove(ActorData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("DELETE FROM actors WHERE id = ?");
            stmt.setInt(1, item.getID());
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<ActorData> update(ActorData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("UPDATE actors SET name = ? WHERE id = ?");
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getID());
            stmt.executeUpdate();
            manager.commit();
        }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<ActorData> add(DataBuilder<ActorData> builder) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            builder.setID(manager.prepareStatement("SELECT NEXTVAL('actors_id_seq')::regclass").executeQuery().getInt(1));
            ActorData data = builder.build();
            var stmt = manager.prepareStatement("INSERT INTO actors (id, name) VALUES (?, ?)");
            stmt.setInt(1, data.getID());
            stmt.setString(2, data.getName());
            stmt.executeUpdate();
            manager.commit();
        }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        } catch (NullDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }
}
