package org.database.dao;

import org.database.TestJPAUtil;
import org.database.entity.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerRepositoryTest {

    private static PlayerRepository repo;

    @BeforeAll
    static void setup() {
        TestJPAUtil.setup();
        repo = new PlayerRepository();
    }

    @AfterAll
    static void teardown() {
        TestJPAUtil.teardown();
    }

    @Test
    @Order(1)
    void createAndFindByUsername() {
        Player player = new Player("alice");
        repo.create(player);

        Player found = repo.findByUsername("alice");
        assertNotNull(found, "Player should be found after creation");
        assertEquals("alice", found.getUsername());
        assertNotNull(found.getId(), "UUID should be assigned");
    }

    @Test
    @Order(2)
    void findByUsername_nonExistent_returnsNull() {
        Player found = repo.findByUsername("nobody_xyz");
        assertNull(found, "Non-existent username should return null");
    }

    @Test
    @Order(3)
    void create_duplicateUsername_throwsException() {
        repo.create(new Player("bob"));
        assertThrows(RuntimeException.class,
                () -> repo.create(new Player("bob")),
                "Duplicate username should throw a persistence exception");
    }
}
