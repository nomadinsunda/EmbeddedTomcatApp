package com.intheeast.service;

import java.util.List;

public interface EntityService<T, C> {

    /**
     * Generic method for posting an entity. Prepares the entity for storage before invoking the callback to do
     * the actual work of saving the entity.
     * 
     * @param entity the entity to save
     * @param callback the callback that handles the persistence logic
     */
    void post(T entity, C callback);
    
    T findById(Long id, C callback);
    
    List<T> findAll(C callback);
    
    void update(T entity, C callback);
    
    void delete(T entity, C callback);
}