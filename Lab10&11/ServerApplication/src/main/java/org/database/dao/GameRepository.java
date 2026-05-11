package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.database.entity.Game;

public class GameRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyLab");

    public void create(Game game) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Game findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM Game g WHERE g.name = :name", Game.class)
                     .setParameter("name", name)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Game update(Game game) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Game merged = em.merge(game);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }
}
