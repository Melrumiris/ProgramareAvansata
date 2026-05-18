package org.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Test helper: sets up an in-memory H2 EMF and installs it into {@link JPAUtil}
 * so repositories use H2 instead of the production PostgreSQL database.
 *
 * Usage:
 * <pre>
 *   @BeforeAll static void setup()    { TestJPAUtil.setup(); }
 *   @AfterAll  static void teardown() { TestJPAUtil.teardown(); }
 * </pre>
 */
public final class TestJPAUtil {

    private static EntityManagerFactory emf;

    private TestJPAUtil() {}

    public static void setup() {
        emf = Persistence.createEntityManagerFactory("TestUnit");
        JPAUtil.setEntityManagerFactory(emf);
    }

    public static void teardown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
