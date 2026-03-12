package com.acasado.opored.controller;

import com.acasado.opored.dto.PublicExaminationDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.service.PublicExaminationService;
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
import java.util.Set;

@RestController
@RequestMapping(
        value = "/api/publicExaminations",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Examinations", description = "Public Examination management endpoints")
public class PublicExaminationController {

    private final PublicExaminationService publicExaminationService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all public examinations")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<PublicExaminationDTO>> getAllPublicExaminations() {
        log.info("getAllPublicExaminations");
        List<PublicExaminationDTO> publicExaminations = publicExaminationService.getAllPublicExaminations();
        return ResponseEntity.ok(publicExaminations);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get public examination by ID")
    @ApiResponse(responseCode = "200", description = "Public examination found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<PublicExaminationDTO> getPublicExaminationById(
            @Parameter(description = "Public Examination ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getPublicExaminationById with id {}", id);
        PublicExaminationDTO publicExaminationDTO = publicExaminationService.getPublicExaminationById(id);
        return ResponseEntity.ok(publicExaminationDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_CREATE)")
    @Operation(summary = "Create public examination")
    @ApiResponse(responseCode = "201", description = "Public examination created")
    @PostMapping
    public ResponseEntity<PublicExaminationDTO> createPublicExamination(
            @RequestBody @NotNull @Valid PublicExaminationDTO publicExaminationDTO) {
        log.info("createPublicExamination with id: {}", publicExaminationDTO.getId());
        PublicExaminationDTO publicExaminationDTOCreated = publicExaminationService.createPublicExamination(publicExaminationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicExaminationDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update public examination")
    @ApiResponse(responseCode = "200", description = "Public examination updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/{id}")
    public ResponseEntity<PublicExaminationDTO> updatePublicExamination(@RequestBody @NotNull @Valid PublicExaminationDTO publicExaminationDTO, @PathVariable @NotNull Integer id)
    {
        log.info("updatePublicExamination");
        PublicExaminationDTO publicExaminationDTOUpdated = publicExaminationService.updatePublicExamination(id,
                publicExaminationDTO.getName(),
                publicExaminationDTO.getDescription(),
                publicExaminationDTO.getCategoryId());
        return ResponseEntity.ok(publicExaminationDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete public examination")
    @ApiResponse(responseCode = "204", description = "Public examination deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublicExamination(
            @Parameter(description = "Public Examination ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deletePublicExamination with id {}", id);
        publicExaminationService.deletePublicExamination(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get students enrolled in a public examination")
    @ApiResponse(responseCode = "200", description = "List of students returned")
    @ApiResponse(responseCode = "404", description = "Public examination not found")
    @GetMapping("/students/{id}")
    public ResponseEntity<Set<StudentSummaryDTO>> getStudents(
            @Parameter(description = "Public Examination ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getStudents associated with publicExamination with id {}", id);
        return ResponseEntity.ok(publicExaminationService.getStudents(id));
    }
}