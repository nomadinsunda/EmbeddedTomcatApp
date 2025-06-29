package com.intheeast.service;

import java.util.List;

public interface EntityCallback<T> {
    
    void post(T entity);
    
    T findById(Long id);
    
    List<T> findAll();
    
    void update(T entity);
    
    void delete(T entity);
}