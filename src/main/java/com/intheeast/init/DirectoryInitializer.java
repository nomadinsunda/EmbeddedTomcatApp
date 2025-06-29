package com.intheeast.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class DirectoryInitializer {

    @Value("${file.upload.dir}")
    private String uploadDirPath;

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadDirPath);

        if (!uploadDir.exists()) {
            if (uploadDir.mkdirs()) {
                System.out.println("Upload directory created at: " + uploadDirPath);
            } else {
                System.out.println("Failed to create upload directory at: " + uploadDirPath);
            }
        } else {
            System.out.println("Upload directory already exists: " + uploadDirPath);
        }
    }
}