package com.example.spring_rest_api.image.service;

import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.common.exception.BusinessException;
import com.example.spring_rest_api.common.exception.InvalidFileException;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageFileService {
    private final ImageFileRepository imageFileRepository;
    private final ArticleRepository articleRepository;

    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));

    private static final Path PROFILE_DIR = PROJECT_ROOT.resolve("uploads/profile");
    private static final Path ARTICLE_DIR = PROJECT_ROOT.resolve("uploads/articles");

    private static final String PROFILE_URL = "/public/profile/";
    private static final String ARTICLE_URL = "/public/article/";

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");


    public ImageFile uploadProfileImage(MultipartFile file, Long userId) {
        return uploadProfileFile(file, userId);
    }

    private ImageFile uploadProfileFile(MultipartFile file, Long userId) {
        String extension = extractAndValidateExtension(file);
        String fileName = generateFileName("profile", extension);
        Path savePath = ImageFileService.PROFILE_DIR.resolve(fileName);

        try {
            if (!Files.exists(ImageFileService.PROFILE_DIR)) {
                Files.createDirectories(ImageFileService.PROFILE_DIR);
            }

            file.transferTo(savePath.toFile());
        } catch (IOException exception) {
            throw new BusinessException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String dbFilePath = ImageFileService.PROFILE_URL + fileName;
        return imageFileRepository.save(ImageFile.createProfileImage(dbFilePath, userId));
    }


    public List<ImageFile> uploadArticleImages(List<MultipartFile> files, Long userId) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        createDirectory();

        return files.stream()
                .map(f -> uploadArticleFile(f, userId))
                .toList();
    }

    private ImageFile uploadArticleFile(MultipartFile file, Long userId) {
        String extension = extractAndValidateExtension(file);
        String fileName = generateFileName("article", extension);
        Path savePath = ImageFileService.ARTICLE_DIR.resolve(fileName);

        try {
            file.transferTo(savePath.toFile());
        } catch (IOException exception) {
            throw new BusinessException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String dbFilePath = ImageFileService.ARTICLE_URL + fileName;
        return imageFileRepository.save(ImageFile.createArticleImage(dbFilePath, userId));
    }


    private void createDirectory() {
        if (!Files.exists(ImageFileService.ARTICLE_DIR)) {
            try {
                Files.createDirectories(ImageFileService.ARTICLE_DIR);
            } catch (IOException exception) {
                throw new BusinessException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private String extractAndValidateExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException(file.getName(), "FILE_NAME_REQUIRED");
        }

        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(file.getName(), "INVALID_EXTENSION");
        }

        return extension;
    }

    private String generateFileName(String prefix, String extension) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID()
                .toString()
                .substring(0, 8);
        return prefix + "-" + timestamp + "-" + uuid + "." + extension;
    }
}
