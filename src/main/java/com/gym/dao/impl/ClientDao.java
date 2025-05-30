package com.gym.dao.impl;

import com.gym.model.Client;
import com.gym.model.ClientStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class ClientDao extends AbstractDao<Client, Long> {
    
    @Override
    public List<Client> findAll() {
        List<Client> clients = super.findAll();
        
        for (Client client : clients) {
            initializeClientProperties(client);
        }
        return clients;
    }
    
    @Override
    public java.util.Optional<Client> findById(Long id) {
        java.util.Optional<Client> clientOpt = super.findById(id);
        clientOpt.ifPresent(this::initializeClientProperties);
        return clientOpt;
    }
    
    public void initializeClientProperties(Client client) {
        
        try {
            java.lang.reflect.Method initMethod = Client.class.getDeclaredMethod("initializeProperties");
            initMethod.setAccessible(true);
            initMethod.invoke(client);
        } catch (Exception e) {
            log.error("Error initializing client properties: {}", e.getMessage());
        }
    }
    
    public ClientDao() {
        super(Client.class);
    }

    public List<Client> findByStatus(ClientStatus status) {
        EntityManager em = getEntityManager();
        try {
            List<Client> clients = em.createQuery(
                "SELECT c FROM Client c WHERE c.status = :status", Client.class)
                .setParameter("status", status)
                .getResultList();
            
            
            for (Client client : clients) {
                initializeClientProperties(client);
            }
            
            return clients;
        } finally {
            em.close();
        }
    }

    public Client findByUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            
            Long clientId = (Long) em.createNativeQuery(
                "SELECT client_id FROM users WHERE id = :userId")
                .setParameter("userId", userId)
                .getSingleResult();

            if (clientId != null) {
                log.info("Found client_id {} for user_id {}", clientId, userId);
                
                Client client = em.find(Client.class, clientId);
                if (client != null) {
                    
                    initializeClientProperties(client);
                    log.info("Found client: {}", client.getFullName());
                    return client;
                }
            }
            
            log.warn("No client found for user_id {}", userId);
            return null;
        } catch (Exception e) {
            log.error("Error finding client by user ID {}: {}", userId, e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Client> findByTrainerId(Long trainerId) {
        EntityManager em = getEntityManager();
        try {
            List<Client> clients = em.createQuery(
                "SELECT ct.client FROM ClientTrainer ct WHERE ct.trainer.id = :trainerId",
                Client.class
            )
            .setParameter("trainerId", trainerId)
            .getResultList();
            
            
            for (Client client : clients) {
                initializeClientProperties(client);
            }
            
            return clients;
        } finally {
            em.close();
        }
    }
    
    public List<Client> searchByName(String name) {
        EntityManager em = getEntityManager();
        try {
            List<Client> clients = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(:name)",
                Client.class
            )
            .setParameter("name", "%" + name + "%")
            .getResultList();
            
            
            for (Client client : clients) {
                initializeClientProperties(client);
            }
            
            return clients;
        } finally {
            em.close();
        }
    }
    
    @Override
    public Client save(Client client) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            
            if (client.getUser() != null && client.getUser().getId() != null) {
                log.info("Saving client with user_id: {}", client.getUser().getId());
            } else {
                log.warn("Attempting to save client without user_id");
            }
            
            
            if (client.getId() == null) {
                em.persist(client);
                log.info("Persisted new client with user_id: {}", 
                    client.getUser() != null ? client.getUser().getId() : "null");
            } 
            
            else {
                client = em.merge(client);
                log.info("Updated existing client with ID: {}", client.getId());
            }
            
            em.getTransaction().commit();
            return client;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Error saving client: {}", e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
}