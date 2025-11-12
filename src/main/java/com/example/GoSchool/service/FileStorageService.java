package com.example.GoSchool.service;

import com.example.GoSchool.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        // Validate file
        if (file.getOriginalFilename() == null || file.getOriginalFilename().contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + file.getOriginalFilename());
        }

        // Check file type
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new RuntimeException("Only PDF, JPEG, PNG files are allowed");
        }

        try {
            // Create subdirectory
            Path targetLocation = this.fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetLocation);

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" +
                    file.getOriginalFilename().replace(" ", "_");

            // Copy file to target location
            Path targetPath = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return subDirectory + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg")
        );
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }
}