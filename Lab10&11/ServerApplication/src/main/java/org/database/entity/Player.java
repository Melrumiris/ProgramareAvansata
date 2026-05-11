package org.database.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
    private List<Game> games = new ArrayList<>();

    public Player() {}

    public Player(String username) {
        this.username = username;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<Game> getGames() { return games; }
    public void setGames(List<Game> games) { this.games = games; }

    @Override
    public String toString() {
        return "Player{id=" + id + ", username='" + username + "'}";
    }
}