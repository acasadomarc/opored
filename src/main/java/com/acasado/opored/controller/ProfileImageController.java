package com.acasado.opored.controller;

import com.acasado.opored.dto.FileUploadResponse;
import com.acasado.opored.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileImageController {

    private final StorageService storageService;

    @PreAuthorize("hasAuthority(@authorities.USER_UPDATE)")
    @Operation(summary = "Upload profile photo")
    @ApiResponse(responseCode = "200", description = "Image uploaded")
    @PostMapping(value = "/upload")
    public ResponseEntity<FileUploadResponse> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {

        String fileUrl = storageService.store(file);

        return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Imagen subida correctamente"));
    }
}