package com.acasado.opored.controller;

import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/professors",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Professors", description = "Professor management endpoints")
public class ProfessorController {

    private final ProfessorService professorService;

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get all professors")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> getAllProfessors() {
        log.info("getAllProfessors");
        List<ProfessorDTO> professors = professorService.getAllProfessors();
        return ResponseEntity.ok(professors);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get professor by ID")
    @ApiResponse(responseCode = "200", description = "Professor found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDTO> getProfessorById(
            @Parameter(description = "Professor ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getProfessorById with id {}", id);
        ProfessorDTO professorDTO = professorService.getProfessorById(id);
        return ResponseEntity.ok(professorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get professor by email")
    @ApiResponse(responseCode = "200", description = "Professor found")
    @GetMapping("/email/{email}")
    public ResponseEntity<ProfessorDTO> getProfessorByEmail(
            @Parameter(description = "Professor email", example = "professor@example.com")
            @PathVariable @NotNull String email) {
        log.info("getProfessorByEmail with email {}", email);
        ProfessorDTO professorDTO = professorService.getProfessorByEmail(email);
        return ResponseEntity.ok(professorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Delete professor by ID")
    @ApiResponse(responseCode = "204", description = "Professor deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfessor(
            @Parameter(description = "Professor ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteProfessor with id {}", id);
        professorService.disableProfessor(id);
        return ResponseEntity.noContent().build();
    }
}