package com.example.spring_rest_api.common.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Getter
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    private final Path root;

    public UploadProperties(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    public Path profileDirectory() {
        return root.resolve("profile");
    }

    public Path articlesDirectory() {
        return root.resolve("articles");
    }
}
