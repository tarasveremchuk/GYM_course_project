package com.gym.dao.impl;

import com.gym.model.Visit;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class VisitDao extends AbstractDao<Visit, Long> {
    
    public VisitDao() {
        super(Visit.class);
    }
    
    public List<Visit> findByClientId(Long clientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Visit v WHERE v.client.id = :clientId " +
                "ORDER BY v.visitDate DESC",
                Visit.class
            )
            .setParameter("clientId", clientId)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public Optional<Visit> findByBookingId(Long bookingId) {
        EntityManager em = getEntityManager();
        try {
            List<Visit> visits = em.createQuery(
                "SELECT v FROM Visit v JOIN Booking b ON v.client.id = b.client.id " +
                "WHERE b.id = :bookingId AND DATE(v.visitDate) = DATE(b.training.scheduledAt)",
                Visit.class
            )
            .setParameter("bookingId", bookingId)
            .getResultList();
            return visits.isEmpty() ? Optional.empty() : Optional.of(visits.get(0));
        } finally {
            em.close();
        }
    }
    
    public List<Visit> findVisitsByMonth(int year, int month) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Visit v WHERE YEAR(v.visitDate) = :year AND MONTH(v.visitDate) = :month",
                Visit.class
            )
            .setParameter("year", year)
            .setParameter("month", month)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Visit> findRecentVisits(int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Visit v ORDER BY v.visitDate DESC",
                Visit.class
            )
            .setMaxResults(limit)
            .getResultList();
        } finally {
            em.close();
        }
    }
}
