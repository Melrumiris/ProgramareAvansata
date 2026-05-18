package org.database.dao;

import jakarta.persistence.EntityManager;
import org.database.JPAUtil;
import org.database.entity.Question;
import org.database.util.QueryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionRepository {

    private static final Logger log = LoggerFactory.getLogger(QuestionRepository.class);

    public void create(Question question) {
        QueryLogger.timedVoid(log, "create(Question)", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(question);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            } finally {
                em.close();
            }
        });
    }

    public List<Question> findAll() {
        return QueryLogger.timed(log, "findAll(Question)", () -> {
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                return em.createQuery("SELECT q FROM Question q", Question.class)
                        .setHint("org.hibernate.cacheable", true)
                        .getResultList();
            } finally {
                em.close();
            }
        });
    }

    public List<Question> findRandomN(int count) {
        List<Question> all = new ArrayList<>(findAll());
        Collections.shuffle(all);
        return all.subList(0, Math.min(count, all.size()));
    }
}
