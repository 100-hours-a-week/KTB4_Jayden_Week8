package com.example.spring_rest_api.image.repository;

import com.example.spring_rest_api.image.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByFilePath(String filePath);
}
