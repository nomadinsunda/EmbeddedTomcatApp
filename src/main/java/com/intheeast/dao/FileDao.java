package com.intheeast.dao;

import java.util.List;

import com.intheeast.entity.FileEntity;
import com.intheeast.entity.Post;

public interface FileDao {
    void save(FileEntity file);
    FileEntity findById(Long id);
    List<FileEntity> findAllByPost(Post post);
}