package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.database.entity.Player;

public class PlayerRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyLab");

    public void create(Player player) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(player);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Player findByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Player p WHERE p.username = :username", Player.class)
                     .setParameter("username", username)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}