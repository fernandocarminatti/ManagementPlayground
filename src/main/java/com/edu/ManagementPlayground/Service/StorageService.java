package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.StorageException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    public StorageService() {
        try {
            Files.createDirectories(StorageContext.DEFAULT.getFolder());
            Files.createDirectories(StorageContext.NOTAFISCAL.getFolder());
            Files.createDirectories(StorageContext.BOLETO.getFolder());
            Files.createDirectories(StorageContext.COMPROVANTEPAGAMENTO.getFolder());
        } catch (Exception ex) {
            throw new StorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, StorageContext fileContext) {
        checkFileName(file.getOriginalFilename());
        String generatedFileName = generateFileName(file.getOriginalFilename());
        try {
            Path targetLocation = fileContext.getFolder().resolve(generatedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return generatedFileName;
        } catch (IOException ex) {
            throw new StorageException("Could not store file " + generatedFileName);
        }
    }

    public Resource loadAsResource(String filename, StorageContext context) {
        checkFileName(StringUtils.cleanPath(filename));
        Path filePath = context.getFolder().resolve(filename);
        checkPathSecurity(filePath);
        return getFileSystemResource(filePath);
    }

    private void checkFileName(String filename){
        if (filename == null || filename.contains("..")) {
            throw new StorageException("Filename contains invalid path sequence " + filename);
        }
    }

    private String generateFileName(String fileName){
        String fileExtension = getFileExtension(fileName);
        return UUID.randomUUID() + fileExtension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            throw new StorageException("File name not supported.");
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new StorageException("File extension is missing or could not be parsed.");
        }
        return fileName.substring(dotIndex);
    }

    private void checkPathSecurity(Path path){
        if (!path.normalize().startsWith(StorageContext.DEFAULT.getFolder().normalize())) {
            throw new StorageException("Cannot access files outside the designated storage directory.");
        }
    }

    private Resource getFileSystemResource(Path filePath){
        Resource resource = new FileSystemResource(filePath);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new StorageException("File could not be found.");
        }
    }
}