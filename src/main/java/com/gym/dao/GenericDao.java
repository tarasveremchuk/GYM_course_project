package com.gym.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(ID id);
    T update(T entity);
    boolean exists(ID id);
} 