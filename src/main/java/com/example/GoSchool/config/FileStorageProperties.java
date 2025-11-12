package com.example.GoSchool.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageProperties {
    private String uploadDir = "uploads";

    // You can add more properties if needed
    private long maxFileSize = 10485760;
    private String[] allowedFileTypes = {"application/pdf", "image/jpeg", "image/png", "image/jpg"};
}