package org.database.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.database.entity.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyLab");

    public void create(Question question) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(question);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Question> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT q FROM Question q", Question.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Question> findRandomN(int count) {
        List<Question> all = new ArrayList<>(findAll());
        Collections.shuffle(all);
        return all.subList(0, Math.min(count, all.size()));
    }
}
