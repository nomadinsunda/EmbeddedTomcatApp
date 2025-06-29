package com.intheeast.dao.hbn;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.intheeast.dao.FileDao;
import com.intheeast.entity.FileEntity;
import com.intheeast.entity.Post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class HbnFileDao implements FileDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(FileEntity file) {
        entityManager.persist(file);
    }

    @Override
    public FileEntity findById(Long id) {
        return entityManager.find(FileEntity.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FileEntity> findAllByPost(Post post) {
        return entityManager.createQuery("FROM FileEntity f WHERE f.post = :post")
                .setParameter("post", post)
                .getResultList();
    }
}