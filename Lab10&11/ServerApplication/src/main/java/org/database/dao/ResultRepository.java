package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.database.JPAUtil;
import org.database.entity.Game;
import org.database.entity.Player;
import org.database.entity.Result;
import org.database.util.QueryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ResultRepository {

    private static final Logger log = LoggerFactory.getLogger(ResultRepository.class);

    public void create(Result result) {
        QueryLogger.timedVoid(log, "create(Result)", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(result);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }

    public List<Result> findByGame(Game game) {
        return QueryLogger.timed(log, "findByGame(" + game.getName() + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                return em.createQuery(
                                "SELECT r FROM Result r WHERE r.game = :game " +
                                "ORDER BY r.score DESC, r.totalResponseTimeMs ASC", Result.class)
                        .setParameter("game", game)
                        .getResultList();
            } finally {
                em.close();
            }
        });
    }

    public List<Result> search(ResultFilterCriteria criteria) {
        return QueryLogger.timed(log, "search(" + criteria + ")", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Result> cq = cb.createQuery(Result.class);
                Root<Result> root = cq.from(Result.class);

                Join<Result, Player> playerJoin = root.join("player", JoinType.INNER);
                Join<Result, Game>   gameJoin   = root.join("game",   JoinType.INNER);

                List<Predicate> predicates = new ArrayList<>();

                if (criteria.playerNamePrefix() != null && !criteria.playerNamePrefix().isBlank()) {
                    predicates.add(cb.like(
                            cb.lower(playerJoin.get("username")),
                            criteria.playerNamePrefix().toLowerCase() + "%"));
                }
                if (criteria.minScore() != null) {
                    predicates.add(cb.ge(root.get("score"), criteria.minScore()));
                }
                if (criteria.gameName() != null && !criteria.gameName().isBlank()) {
                    predicates.add(cb.equal(
                            cb.lower(gameJoin.get("name")),
                            criteria.gameName().toLowerCase()));
                }

                cq.where(predicates.toArray(new Predicate[0]));
                cq.orderBy(cb.desc(root.get("score")), cb.asc(root.get("totalResponseTimeMs")));

                return em.createQuery(cq).getResultList();
            } finally {
                em.close();
            }
        });
    }
}
