package com.acasado.opored.service;

import com.acasado.opored.exception.FileManagementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    private StorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new StorageService(tempDir.toString());
    }

    @Test
    void When_StoreValidFile_Expect_UrlReturned() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // Act
        String url = storageService.store(file);

        // Assert
        assertNotNull(url);
        assertTrue(url.contains("/uploads/"));
        String filename = url.substring(url.lastIndexOf("/") + 1);
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void Expect_IllegalArgumentException_When_StoreEmptyFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> storageService.store(file));
    }

    @Test
    void When_DeleteValidUrl_Expect_FileRemoved() throws IOException {
        // Arrange
        Path file = tempDir.resolve("to-delete.txt");
        Files.write(file, "content".getBytes());
        String url = "/uploads/to-delete.txt";

        // Act
        storageService.delete(url);

        // Assert
        assertFalse(Files.exists(file));
    }

    @Test
    void When_DeleteEmptyUrl_Expect_NoAction() {
        // Act & Assert
        assertDoesNotThrow(() -> storageService.delete(null));
        assertDoesNotThrow(() -> storageService.delete("   "));
    }

    @Test
    void Expect_FileManagementException_When_IoErrorDuringStore() {
        // Arrange
        // We use a non-existent directory to trigger an IOException
        StorageService invalidService = new StorageService("/non/existent/dir");
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        // Act & Assert
        assertThrows(FileManagementException.class, () -> invalidService.store(file));
    }
}
