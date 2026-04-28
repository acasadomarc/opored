 package com.acasado.opored.service;

import com.acasado.opored.exception.FileManagementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService{

    private final Path rootLocation;

    // Get the path to upload the file
    public StorageService(@Value("${file.storage.upload.dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
    }

    public String store(MultipartFile file) {
        // Path where the file can be searched for
        final String BASE_URL = "/uploads/";

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("No se puede guardar un archivo vacío.");
            }

            // Generate name with random ID
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID() + extension;

            // It will be stored in the /storage/uploads directory in the root dir of the filesystem
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

            // Security checks
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new SecurityException("No se puede guardar el archivo fuera del directorio actual.");
            }

            // Save file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return  BASE_URL + newFilename;

        } catch (IOException e) {
            throw new FileManagementException("Fallo al guardar el archivo: " + e);
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            if (filename.isEmpty()) {
                throw new IllegalArgumentException("URL de archivo no válida: " + fileUrl);
            }

            Path fileToDelete = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

            // Security check
            if (!fileToDelete.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new SecurityException("No se puede eliminar un archivo fuera del directorio actual.");
            }

            Files.deleteIfExists(fileToDelete);

        } catch (IOException e) {
            throw new FileManagementException("Fallo al intentar eliminar el archivo: " + e);
        }
    }
}