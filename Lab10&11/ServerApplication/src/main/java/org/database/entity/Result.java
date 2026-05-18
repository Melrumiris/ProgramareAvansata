package org.database.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Audited
@Entity
@Table(name = "result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "total_response_time_ms", nullable = false)
    private long totalResponseTimeMs;

    public Result() {}

    public Result(Player player, Game game, int score, long totalResponseTimeMs) {
        this.player = player;
        this.game = game;
        this.score = score;
        this.totalResponseTimeMs = totalResponseTimeMs;
    }

    public UUID getId() { return id; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public long getTotalResponseTimeMs() { return totalResponseTimeMs; }
    public void setTotalResponseTimeMs(long totalResponseTimeMs) { this.totalResponseTimeMs = totalResponseTimeMs; }

    @Override
    public String toString() {
        return "Result{player=" + (player != null ? player.getUsername() : "null") +
               ", game=" + (game != null ? game.getName() : "null") +
               ", score=" + score +
               ", totalResponseTimeMs=" + totalResponseTimeMs + "}";
    }
}
