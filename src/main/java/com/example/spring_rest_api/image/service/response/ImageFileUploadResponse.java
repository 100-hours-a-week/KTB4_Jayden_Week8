package com.example.spring_rest_api.image.service.response;

import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageFileUploadResponse {

    private String fileUrl;

    public static ImageFileUploadResponse of(String fileUrl) {
        return new ImageFileUploadResponse(fileUrl);
    }

    public static ImageFileUploadResponse from(ImageFile imageFile) {
        return of(ImageFileUtil.toFullUrl(imageFile.getFilePath()));
    }
}
