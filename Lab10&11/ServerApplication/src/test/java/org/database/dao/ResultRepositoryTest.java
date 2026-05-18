package org.database.dao;

import org.database.TestJPAUtil;
import org.database.entity.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResultRepositoryTest {

    private static PlayerRepository playerRepo;
    private static GameRepository gameRepo;
    private static ResultRepository resultRepo;

    @BeforeAll
    static void setup() {
        TestJPAUtil.setup();
        playerRepo = new PlayerRepository();
        gameRepo   = new GameRepository();
        resultRepo = new ResultRepository();

        // Seed data
        Player alice = new Player("alice_r");
        Player bob   = new Player("bob_r");
        playerRepo.create(alice);
        playerRepo.create(bob);

        Game g1 = new Game("Trivia");
        g1.setState(GameState.FINISHED);
        gameRepo.create(g1);

        Game g2 = new Game("Quiz");
        g2.setState(GameState.FINISHED);
        gameRepo.create(g2);

        // Re-fetch managed instances from DB
        alice = playerRepo.findByUsername("alice_r");
        bob   = playerRepo.findByUsername("bob_r");
        g1 = gameRepo.findByName("Trivia");
        g2 = gameRepo.findByName("Quiz");

        resultRepo.create(new Result(alice, g1, 4, 12000));
        resultRepo.create(new Result(bob,   g1, 2,  8000));
        resultRepo.create(new Result(alice, g2, 1, 15000));
    }

    @AfterAll
    static void teardown() {
        TestJPAUtil.teardown();
    }

    @Test
    @Order(1)
    void findByGame_returnsResultsOrderedByScore() {
        Game g = gameRepo.findByName("Trivia");
        List<Result> results = resultRepo.findByGame(g);
        assertEquals(2, results.size());
        assertEquals("alice_r", results.get(0).getPlayer().getUsername(), "Alice should be first (higher score)");
    }

    @Test
    @Order(2)
    void search_noFilters_returnsAll() {
        List<Result> all = resultRepo.search(ResultFilterCriteria.empty());
        assertEquals(3, all.size());
    }

    @Test
    @Order(3)
    void search_filterByPlayerPrefix() {
        List<Result> results = resultRepo.search(new ResultFilterCriteria("alice", null, null));
        assertEquals(2, results.size());
        results.forEach(r -> assertTrue(r.getPlayer().getUsername().startsWith("alice")));
    }

    @Test
    @Order(4)
    void search_filterByMinScore() {
        List<Result> results = resultRepo.search(new ResultFilterCriteria(null, 3, null));
        assertEquals(1, results.size());
        assertEquals(4, results.get(0).getScore());
    }

    @Test
    @Order(5)
    void search_filterByGameName() {
        List<Result> results = resultRepo.search(new ResultFilterCriteria(null, null, "Quiz"));
        assertEquals(1, results.size());
        assertEquals("Quiz", results.get(0).getGame().getName());
    }

    @Test
    @Order(6)
    void search_combinedFilters() {
        // alice with minscore=3 in Trivia
        List<Result> results = resultRepo.search(new ResultFilterCriteria("alice", 3, "Trivia"));
        assertEquals(1, results.size());
        assertEquals("alice_r", results.get(0).getPlayer().getUsername());
        assertTrue(results.get(0).getScore() >= 3);
    }
}
