package com.acasado.opored.controller;

import com.acasado.opored.dto.RatingProfessorDTO;
import com.acasado.opored.service.RatingProfessorService;
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
        value = "/api/ratingProfessors",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rating Professors", description = "Professor rating management endpoints")
public class RatingProfessorController {

    private final RatingProfessorService ratingProfessorService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all professor ratings")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<RatingProfessorDTO>> getAllRatingProfessors() {
        log.info("getAllRatingProfessors");
        List<RatingProfessorDTO> ratingProfessors = ratingProfessorService.getAllRatingProfessors();
        return ResponseEntity.ok(ratingProfessors);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get rating by ID")
    @ApiResponse(responseCode = "200", description = "Rating found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<RatingProfessorDTO> getRatingProfessorById(
            @Parameter(description = "Rating ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getRatingProfessorById with id {}", id);
        RatingProfessorDTO ratingProfessorDTO = ratingProfessorService.getRatingProfessorById(id);
        return ResponseEntity.ok(ratingProfessorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_CREATE)")
    @Operation(summary = "Create a professor rating")
    @ApiResponse(responseCode = "201", description = "Rating created")
    @PostMapping
    public ResponseEntity<RatingProfessorDTO> createRatingProfessor(
            @RequestBody @NotNull @Valid RatingProfessorDTO ratingProfessorDTO) {
        log.info("createRatingProfessor with id: {}", ratingProfessorDTO.getId());
        RatingProfessorDTO ratingProfessorDTOCreated = ratingProfessorService.createRatingProfessor(ratingProfessorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingProfessorDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_UPDATE)")
    @Operation(summary = "Update my professor rating")
    @ApiResponse(responseCode = "200", description = "Rating updated")
    @ApiResponse(responseCode = "404", description = "Rating not found or unauthorized")
    @PutMapping("/me/id/{id}")
    public ResponseEntity<RatingProfessorDTO> updateMyRatingProfessor(@PathVariable @NotNull Integer id, @RequestBody @NotNull @Valid RatingProfessorDTO ratingProfessorDTO)
    {
        log.info("updateRatingProfessor");
        RatingProfessorDTO ratingProfessorDTOUpdated = ratingProfessorService.updateMyRatingProfessor(
                id,
                ratingProfessorDTO.getTitle(),
                ratingProfessorDTO.getScore(),
                ratingProfessorDTO.getComment()
        );
        return ResponseEntity.ok(ratingProfessorDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_DELETE)")
    @Operation(summary = "Delete professor rating")
    @ApiResponse(responseCode = "204", description = "Rating deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRatingProfessor(
            @Parameter(description = "Rating ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteRatingProfessor with id {}", id);
        ratingProfessorService.deleteRatingProfessor(id);
        return ResponseEntity.noContent().build();
    }
}