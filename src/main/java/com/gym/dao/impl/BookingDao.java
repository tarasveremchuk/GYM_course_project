package com.gym.dao.impl;

import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDao extends AbstractDao<Booking, Long> {
    public BookingDao() {
        super(Booking.class);
    }
    
    public List<Booking> findByClientId(Long clientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Booking b WHERE b.client.id = :clientId " +
                "ORDER BY b.bookingTime DESC",
                Booking.class
            )
            .setParameter("clientId", clientId)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Booking> findByTrainingId(Long trainingId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Booking b WHERE b.training.id = :trainingId " +
                "ORDER BY b.bookingTime DESC",
                Booking.class
            )
            .setParameter("trainingId", trainingId)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Booking> findUpcomingBookings(Long clientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM Booking b WHERE b.client.id = :clientId " +
                "AND b.training.scheduledAt > :now AND b.status = :status " +
                "ORDER BY b.training.scheduledAt",
                Booking.class
            )
            .setParameter("clientId", clientId)
            .setParameter("now", LocalDateTime.now())
            .setParameter("status", BookingStatus.BOOKED)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public long countActiveBookingsForTraining(Long trainingId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.training.id = :trainingId " +
                "AND b.status = :status",
                Long.class
            )
            .setParameter("trainingId", trainingId)
            .setParameter("status", BookingStatus.BOOKED)
            .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public int getBookedCount(Long trainingId) {
        EntityManager em = getEntityManager();
        try {
            return ((Number) em.createQuery(
                "SELECT COUNT(b) FROM Booking b " +
                "WHERE b.training.id = :trainingId AND b.status = :status"
            )
            .setParameter("trainingId", trainingId)
            .setParameter("status", BookingStatus.BOOKED)
            .getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public void cancelBookingsForTraining(Long trainingId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(
                "UPDATE Booking b SET b.status = :newStatus " +
                "WHERE b.training.id = :trainingId AND b.status = :oldStatus"
            )
            .setParameter("trainingId", trainingId)
            .setParameter("oldStatus", BookingStatus.BOOKED)
            .setParameter("newStatus", BookingStatus.CANCELLED)
            .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
} 