package com.acasado.opored.controller;

import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.service.RatingCourseService;
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
        value = "/api/ratingCourses",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rating Courses", description = "Course rating management endpoints")
public class RatingCourseController {

    private final RatingCourseService ratingCourseService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all course ratings")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<RatingCourseDTO>> getAllRatingCourses() {
        log.info("getAllRatingCourses");
        List<RatingCourseDTO> ratingCourses = ratingCourseService.getAllRatingCourses();
        return ResponseEntity.ok(ratingCourses);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get rating by ID")
    @ApiResponse(responseCode = "200", description = "Rating found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<RatingCourseDTO> getRatingCourseById(
            @Parameter(description = "Rating ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getRatingCourseById with id {}", id);
        RatingCourseDTO ratingCourseDTO = ratingCourseService.getRatingCourseById(id);
        return ResponseEntity.ok(ratingCourseDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_CREATE)")
    @Operation(summary = "Create a course rating")
    @ApiResponse(responseCode = "201", description = "Rating created")
    @PostMapping
    public ResponseEntity<RatingCourseDTO> createRatingCourse(
            @RequestBody @NotNull @Valid RatingCourseDTO ratingCourseDTO) {
        log.info("createRatingCourse with id: {}", ratingCourseDTO.getId());
        RatingCourseDTO ratingCourseDTOCreated = ratingCourseService.createRatingCourse(ratingCourseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingCourseDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_UPDATE)")
    @Operation(summary = "Update my course rating")
    @ApiResponse(responseCode = "200", description = "Rating updated")
    @ApiResponse(responseCode = "404", description = "Rating not found or unauthorized")
    @PutMapping("/me/id/{id}")
    public ResponseEntity<RatingCourseDTO> updateMyRatingCourse(@RequestBody @NotNull @Valid RatingCourseDTO ratingCourseDTO)
    {
        log.info("updateRatingCourse");
        RatingCourseDTO ratingCourseDTOUpdated = ratingCourseService.updateMyRatingCourse(
                ratingCourseDTO.getId(),
                ratingCourseDTO.getTitle(),
                ratingCourseDTO.getScore(),
                ratingCourseDTO.getComment()
        );
        return ResponseEntity.ok(ratingCourseDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_DELETE)")
    @Operation(summary = "Delete course rating")
    @ApiResponse(responseCode = "204", description = "Rating deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRatingCourse(
            @Parameter(description = "Rating ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteRatingCourse with id {}", id);
        ratingCourseService.deleteRatingCourse(id);
        return ResponseEntity.noContent().build();
    }
}