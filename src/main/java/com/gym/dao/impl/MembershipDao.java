package com.gym.dao.impl;

import com.gym.model.Membership;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class MembershipDao extends AbstractDao<Membership, Long> {
    public MembershipDao() {
        super(Membership.class);
    }
    
    public List<Membership> findActiveByClientId(Long clientId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Membership m WHERE m.client.id = :clientId " +
                "AND m.endDate >= :currentDate",
                Membership.class
            )
            .setParameter("clientId", clientId)
            .setParameter("currentDate", LocalDate.now())
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Membership> findExpiringMemberships(LocalDate beforeDate) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Membership m WHERE m.endDate <= :beforeDate " +
                "AND m.endDate >= :currentDate",
                Membership.class
            )
            .setParameter("beforeDate", beforeDate)
            .setParameter("currentDate", LocalDate.now())
            .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Membership> findByType(Membership.MembershipType type) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Membership m WHERE m.type = :type",
                Membership.class
            )
            .setParameter("type", type)
            .getResultList();
        } finally {
            em.close();
        }
    }
} 