package com.intheeast.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.intheeast.dao.FileDao;
import com.intheeast.entity.FileEntity;
import com.intheeast.entity.Post;

@Service
public class FileService {

	@Value("${file.upload.dir}")
    private String uploadDirPath;
	
    @Autowired
    private FileDao fileDao;

    @Transactional
    public void storeFile(MultipartFile file, Post post, String dir) throws IOException {
        String fileName = file.getOriginalFilename();

        // FileEntity 저장
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(fileName);
        fileEntity.setFilePath(dir);
        fileEntity.setPost(post);
        fileDao.save(fileEntity);
        
        post.addFile(fileEntity);        
    }

    public FileEntity findById(Long fileId) {
        return fileDao.findById(fileId);
    }

    public List<FileEntity> findAllByPost(Post post) {
        return fileDao.findAllByPost(post);
    }
}