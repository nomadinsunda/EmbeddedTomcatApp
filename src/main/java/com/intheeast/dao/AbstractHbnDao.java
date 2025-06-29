package com.intheeast.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractHbnDao<T> {
    
    private Class<T> entityClass;

    @Autowired
    private SessionFactory sessionFactory;

    // 엔티티 클래스 타입을 설정하는 생성자
    public AbstractHbnDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(T entity) {
        getSession().saveOrUpdate(entity);
//        getSession().persist(entity);
    }

    public void update(T entity) {
        getSession().saveOrUpdate(entity);
    }

    public void delete(T entity) {
        getSession().delete(entity);
    }

    public T findById(Long id) {
        return getSession().get(entityClass, id);
    }

    public List<T> findAll() {
        return getSession()
                .createQuery("from " + entityClass.getName(), entityClass)
                .getResultList();
    }
}
