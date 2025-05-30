package com.gym.dao.impl;

import com.gym.dao.GenericDao;
import com.gym.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements GenericDao<T, ID> {
    private final Class<T> entityClass;
    protected final EntityManagerFactory emf;

    public AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.emf = EntityManagerUtil.getEntityManagerFactory();
    }

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            
            if (isNew(entity)) {
                em.persist(entity);
                em.getTransaction().commit();
                return entity;
            } else {
                T mergedEntity = em.merge(entity);
                em.getTransaction().commit();
                return mergedEntity;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    
    private boolean isNew(T entity) {
        try {
            
            java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(entity);
            return id == null;
        } catch (Exception e) {
            
            return true;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(entityClass);
            criteriaQuery.from(entityClass);
            return em.createQuery(criteriaQuery).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
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

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return updatedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean exists(ID id) {
        return findById(id).isPresent();
    }

    protected Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }
}