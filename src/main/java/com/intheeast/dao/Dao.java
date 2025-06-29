package com.intheeast.dao;

import java.util.List;

public interface Dao<T> {
    
    // 엔티티 저장
    void save(T entity);
    
    // 엔티티 업데이트
    void update(T entity);
    
    // 엔티티 삭제
    void delete(T entity);
    
    // ID로 엔티티 찾기
    T findById(Long id);
    
    // 모든 엔티티 목록 조회
    List<T> findAll();
}