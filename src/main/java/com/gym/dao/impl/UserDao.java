package com.gym.dao.impl;

import com.gym.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class UserDao extends AbstractDao<User, Long> {
    public UserDao() {
        super(User.class);
    }
    
    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(
                em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
} 