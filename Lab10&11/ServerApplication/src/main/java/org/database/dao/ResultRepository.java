package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.database.entity.Game;
import org.database.entity.Result;

import java.util.List;

public class ResultRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyLab");

    public void create(Result result) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(result);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Result> findByGame(Game game) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Result r WHERE r.game = :game ORDER BY r.score DESC, r.totalResponseTimeMs ASC", Result.class)
                     .setParameter("game", game)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
