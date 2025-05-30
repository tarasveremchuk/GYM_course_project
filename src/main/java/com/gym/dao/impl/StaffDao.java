package com.gym.dao.impl;

import com.gym.model.Staff;
import com.gym.model.StaffRole;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class StaffDao extends AbstractDao<Staff, Long> {
    public StaffDao() {
        super(Staff.class);
    }
    
    public List<Staff> searchStaff(String query) {
        EntityManager em = getEntityManager();
        try {
            String searchPattern = "%" + query.toLowerCase() + "%";
            return em.createQuery(
                "SELECT s FROM Staff s WHERE LOWER(s.fullName) LIKE :pattern " +
                "OR LOWER(s.email) LIKE :pattern " +
                "OR LOWER(s.phoneNumber) LIKE :pattern " +
                "ORDER BY s.fullName",
                Staff.class
            )
            .setParameter("pattern", searchPattern)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Staff> findTrainers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Staff s WHERE s.role = :role " +
                "ORDER BY s.fullName",
                Staff.class
            )
            .setParameter("role", StaffRole.TRAINER)
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Staff> findActiveTrainers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Staff s WHERE s.role = :role " +
                "AND s.active = true ORDER BY s.fullName",
                Staff.class
            )
            .setParameter("role", StaffRole.TRAINER)
            .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Staff> findTrainerByClientId(Long clientId) {
        EntityManager em = getEntityManager();
        try {
            List<Staff> results = em.createQuery(
                "SELECT ct.trainer FROM ClientTrainer ct " +
                "WHERE ct.id.clientId = :clientId AND ct.trainer.role = :role",
                Staff.class
            )
            .setParameter("clientId", clientId)
            .setParameter("role", StaffRole.TRAINER)
            .getResultList();
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }
}