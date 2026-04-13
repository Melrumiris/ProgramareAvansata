package gov.Lab6.dao;

import gov.Lab6.conn.DBConnManager;
import gov.Lab6.data.ActorData;
import gov.Lab6.data.CharacterData;
import gov.Lab6.data.builder.ActorBuilder;
import gov.Lab6.data.builder.CharacterBuilder;
import gov.Lab6.data.builder.DataBuilder;
import gov.Lab6.data.builder.MovieBuilder;
import gov.Lab6.exception.IllegalDataException;
import gov.Lab6.exception.NullDataException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CharacterDAO implements DAO<CharacterData> {

    @Override
    public Optional<CharacterData> get(int id) {
        Connection manager = DBConnManager.getInstance().getConnection();
        CharacterBuilder builder = new CharacterBuilder();
        try {
            var stmt = manager.prepareStatement("SELECT * FROM characters JOIN actors ON actor_id = actors.id WHERE characters.id = ?");
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                builder.setID(rs.getInt("id"));
                builder.setName(rs.getString("name"));
                builder.setActor(new ActorBuilder().setID(rs.getInt("actor_id")).setName(rs.getString("actors.name")).build());
                builder.setMovie(new MovieDAO().get(rs.getInt("movie_id")).orElseThrow(() -> new IllegalDataException("Movie not found")));
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

//    public List<CharacterData> getByName(String name) {
//        Connection manager = DBConnManager.getInstance().getConnection();
//       ActorBuilder builder = new ActorBuilder();
//        List<ActorData> result = new ArrayList<>();
//        try {
//            var stmt = manager.prepareStatement("SELECT * FROM actors WHERE name = ?");
//            stmt.setString(1, name);
//            var rs = stmt.executeQuery();
//            while (rs.next()) {
//                builder.setID(rs.getInt("id"));
//                builder.setName(rs.getString("name"));
//                result.add(builder.build());
//            }
//            return result;
//        } catch (SQLException e){
//            System.err.println("Failed to execute query:" + e.getMessage());
//            e.printStackTrace();
//        } catch (NullDataException e) {
//            System.err.println("Failed to build data object:" + e.getMessage());
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    @Override
    public DAO<CharacterData> remove(CharacterData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("DELETE FROM characters WHERE id = ?");
            stmt.setInt(1, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<CharacterData> update(CharacterData item) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var stmt = manager.prepareStatement("UPDATE characters SET name = ?, actor_id = ?, movie_id = ? WHERE id = ?");
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getId());
            stmt.setInt(3, item.getActor().getId());
            stmt.setInt(4, item.getMovie().getId());
            stmt.executeUpdate();
                    }catch (SQLException e){
            System.err.println("Failed to execute query:" + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public DAO<CharacterData> add(DataBuilder<CharacterData> builder) {
        Connection manager = DBConnManager.getInstance().getConnection();
        try {
            var seqRs = manager.prepareStatement("SELECT NEXTVAL('characters_id_seq')").executeQuery();
            seqRs.next();
            builder.setID(seqRs.getInt(1));
            CharacterData data = builder.build();
            var stmt = manager.prepareStatement("INSERT INTO characters (id, actor_id, movie_id, name) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, data.getId());
            stmt.setInt(2, data.getActor().getId());
            stmt.setInt(3, data.getMovie().getId());
            stmt.setString(4, data.getName());
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

    public Map<Integer, Set<Integer>> getMovieActorSets() {
        Connection conn = DBConnManager.getInstance().getConnection();
        Map<Integer, Set<Integer>> result = new HashMap<>();
        try {
            var rs = conn.prepareStatement("SELECT movie_id, actor_id FROM characters WHERE actor_id IS NOT NULL")
                         .executeQuery();
            while (rs.next()) {
                int movieId = rs.getInt("movie_id");
                int actorId = rs.getInt("actor_id");
                result.computeIfAbsent(movieId, k -> new HashSet<>()).add(actorId);
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute query: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
