package com.gym.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;


@Slf4j
public class HibernateUtil {
    

    public static void doInTransaction(Consumer<Session> operation) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        Transaction transaction = null;
        
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            operation.accept(session);
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Transaction failed", e);
            throw new RuntimeException("Transaction failed", e);
        }
    }
    

    public static <T> T doInTransactionWithResult(Function<Session, T> operation) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        Transaction transaction = null;
        
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            T result = operation.apply(session);
            
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Transaction failed", e);
            throw new RuntimeException("Transaction failed", e);
        }
    }
    

    public static <T> T doInSession(Function<Session, T> operation) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        
        try (Session session = sessionFactory.openSession()) {
            return operation.apply(session);
        } catch (Exception e) {
            log.error("Session operation failed", e);
            throw new RuntimeException("Session operation failed", e);
        }
    }
    

    public static <T> T save(T entity) {
        return doInTransactionWithResult(session -> {
            session.persist(entity);
            return entity;
        });
    }
    

    public static <T> T update(T entity) {
        return doInTransactionWithResult(session -> {
            session.merge(entity);
            return entity;
        });
    }
    

    public static void delete(Object entity) {
        doInTransaction(session -> session.remove(entity));
    }
    

    public static <T> T findById(Class<T> entityClass, Object id) {
        return doInSession(session -> session.get(entityClass, id));
    }
}
