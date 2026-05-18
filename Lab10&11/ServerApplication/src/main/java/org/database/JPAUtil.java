package org.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton holder for the JPA EntityManagerFactory.
 * Creating an EMF is expensive; the entire application shares one instance.
 *
 * In tests the EMF can be replaced via {@link #setEntityManagerFactory(EntityManagerFactory)}
 * to point at an in-memory H2 persistence unit instead of the production DB.
 */
public final class JPAUtil {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("MyLab");

    private JPAUtil() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Replaces the shared EMF. Intended for test use only — allows switching
     * to an in-memory persistence unit without touching production config.
     */
    public static void setEntityManagerFactory(EntityManagerFactory override) {
        emf = override;
    }

    /** Call once on application shutdown to release all JPA/JDBC resources. */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
