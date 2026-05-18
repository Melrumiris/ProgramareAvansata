package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.database.JPAUtil;
import org.database.entity.Game;
import org.database.entity.GameState;
import org.database.util.QueryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GameRepository {

    private static final Logger log = LoggerFactory.getLogger(GameRepository.class);

    public void create(Game game) {
        QueryLogger.timedVoid(log, "create(Game:" + game.getName() + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(game);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }

    public Game findByName(String name) {
        return QueryLogger.timed(log, "findByName(" + name + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                return em.createQuery("SELECT g FROM Game g WHERE g.name = :name", Game.class)
                        .setParameter("name", name)
                        .setHint("org.hibernate.cacheable", true)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            } finally {
                em.close();
            }
        });
    }

    public void updateGameState(UUID id, GameState newState) {
        QueryLogger.timedVoid(log, "updateGameState(" + id + ", " + newState + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                em.createQuery("UPDATE Game g SET g.state = :state WHERE g.id = :id")
                        .setParameter("state", newState)
                        .setParameter("id", id)
                        .executeUpdate();
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }

    public Game update(Game game) {
        return QueryLogger.timed(log, "update(Game:" + game.getName() + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                Game merged = em.merge(game);
                em.getTransaction().commit();
                return merged;
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }
}
