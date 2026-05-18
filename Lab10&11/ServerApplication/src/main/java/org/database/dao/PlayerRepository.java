package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.database.JPAUtil;
import org.database.entity.Player;
import org.database.util.QueryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerRepository {

    private static final Logger log = LoggerFactory.getLogger(PlayerRepository.class);

    public void create(Player player) {
        QueryLogger.timedVoid(log, "create(Player:" + player.getUsername() + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(player);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }

    public Player findByUsername(String username) {
        return QueryLogger.timed(log, "findByUsername(" + username + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                return em.createQuery(
                                "SELECT p FROM Player p WHERE p.username = :username", Player.class)
                        .setParameter("username", username)
                        .setHint("org.hibernate.cacheable", true)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            } finally {
                em.close();
            }
        });
    }
}