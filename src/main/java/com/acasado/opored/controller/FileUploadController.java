package com.acasado.opored.controller;

import com.acasado.opored.dto.FileUploadResponse;
import com.acasado.opored.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final StorageService storageService;

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Upload any file (PDF, video, image)")
    @PostMapping(value = "/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("FileUploaded");
        String fileUrl = storageService.store(file);
        return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Archivo subido correctamente"));
    }
}