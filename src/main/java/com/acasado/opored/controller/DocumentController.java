package com.acasado.opored.controller;

import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/documents",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documents", description = "Document management endpoints")
public class DocumentController {

    private final DocumentService documentService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all documents")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        log.info("getAllDocuments");
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new document")
    @ApiResponse(responseCode = "201", description = "Document created")
    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(
            @RequestBody @NotNull @Valid DocumentDTO documentDTO) {
        log.info("createDocument");
        DocumentDTO documentDTOCreated = documentService.createDocument(documentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update a document")
    @ApiResponse(responseCode = "200", description = "Document updated")
    @ApiResponse(responseCode = "404", description = "Document not found")
    @PutMapping("/me")
    public ResponseEntity<DocumentDTO> updateMyDocument(
            @RequestBody @NotNull @Valid DocumentDTO documentDTO) {
        log.info("updateDocument");
        DocumentDTO documentDTOUpdated = documentService.updateDocument(documentDTO);
        return ResponseEntity.ok(documentDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE)")
    @Operation(summary = "Delete document")
    @ApiResponse(responseCode = "204", description = "Document deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @Parameter(description = "Document ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCourse with id {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}