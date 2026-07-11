package com.example.spring_rest_api.image.service.response;

import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListImageFileUploadResponse {

    private List<String> fileUrls;

    public static ListImageFileUploadResponse of(List<String> fileUrls) {
        return new ListImageFileUploadResponse(fileUrls);
    }

    public static ListImageFileUploadResponse from(List<ImageFile> imageFiles) {
        return of(
                imageFiles.stream()
                        .map(ImageFile::getFilePath)
                        .map(ImageFileUtil::toFullUrl)
                        .toList()
        );
    }

}
