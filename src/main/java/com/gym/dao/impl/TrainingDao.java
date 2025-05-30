package com.gym.dao.impl;

import com.gym.model.Training;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class TrainingDao extends AbstractDao<Training, Long> {
    public TrainingDao() {
        super(Training.class);
    }
    
    @Override
    public List<Training> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT t FROM Training t LEFT JOIN FETCH t.trainer " +
                "ORDER BY t.scheduledAt",
                Training.class
            )
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Training> findUpcomingTrainings() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT t FROM Training t WHERE t.scheduledAt > :now " +
                "ORDER BY t.scheduledAt",
                Training.class
            )
            .setParameter("now", LocalDateTime.now())
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Training> findByTrainerId(Long trainerId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT t FROM Training t WHERE t.trainer.id = :trainerId " +
                "ORDER BY t.scheduledAt",
                Training.class
            )
            .setParameter("trainerId", trainerId)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Training> findAvailableTrainings() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT t FROM Training t WHERE t.scheduledAt > :now " +
                "AND (SELECT COUNT(b) FROM Booking b WHERE b.training = t AND b.status = 'booked') < t.capacity " +
                "ORDER BY t.scheduledAt",
                Training.class
            )
            .setParameter("now", LocalDateTime.now())
            .getResultList();
        } finally {
            em.close();
        }
    }
} 