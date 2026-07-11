package com.example.spring_rest_api.image.controller;

import com.example.spring_rest_api.common.response.ApiResponse;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.service.ImageFileService;
import com.example.spring_rest_api.image.service.response.ImageFileUploadResponse;
import com.example.spring_rest_api.image.service.response.ListImageFileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageFileController {
    private final ImageFileService imageFileService;

    @PostMapping("/users/me/profile-image")
    public ResponseEntity<ApiResponse<ImageFileUploadResponse>> uploadProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("profileImage") MultipartFile file
            ) {
        ImageFile savedFile = imageFileService.uploadProfileImage(file, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "profile_image_upload_success",
                        ImageFileUploadResponse.from(savedFile)
                ));
    }

    @PostMapping("/articles/content-image")
    public ResponseEntity<ApiResponse<ListImageFileUploadResponse>> uploadContentImages(
            @AuthenticationPrincipal Long userId,
            @RequestPart("contentImages") List<MultipartFile> files
            ) {
        List<ImageFile> savedFiles = imageFileService.uploadArticleImages(files, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "profile_image_upload_success",
                        ListImageFileUploadResponse.from(savedFiles)
                ));
    }
}
