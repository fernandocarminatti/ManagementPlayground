package com.edu.ManagementPlayground.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private final Path STORAGE_LOCATION;

    public StorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.STORAGE_LOCATION = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.STORAGE_LOCATION);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, long supplierId) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }

        String uniqueFileName = supplierId + "_" + originalFileName;

        try {
            Path targetLocation = this.STORAGE_LOCATION.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadAsResource(String filename) { // filename is your fileReference
        try {
            String cleanedFilename = StringUtils.cleanPath(filename);
            if (cleanedFilename.contains("..")) {
                throw new RuntimeException(
                        "Cannot store file with relative path outside current directory " + cleanedFilename);
            }

            Path file = this.STORAGE_LOCATION.resolve(cleanedFilename);
            // Additional security: ensure the resolved path is still under the rootLocation
            if (!file.normalize().startsWith(this.STORAGE_LOCATION.normalize())) {
                throw new RuntimeException(
                        "Cannot access files outside the designated storage directory: " + cleanedFilename);
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + cleanedFilename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
}