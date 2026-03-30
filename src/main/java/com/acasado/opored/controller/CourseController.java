package com.acasado.opored.controller;

import com.acasado.opored.dto.ContentDTO;
import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.service.CourseService;
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
        value = "/api/courses",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ) || hasAuthority(@authorities.PROFESSOR_READ)")
    @Operation(summary = "Get all courses")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        log.info("getAllCourses");
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get course by ID")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getCourseById with id {}", id);
        CourseDTO courseDTO = courseService.getCourseById(id);
        return ResponseEntity.ok(courseDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new course")
    @ApiResponse(responseCode = "201", description = "Course created")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @RequestBody @NotNull @Valid CourseDTO courseDTO) {
        log.info("createCourse with id: {}", courseDTO.getId());
        CourseDTO courseDTOCreated = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update my course details")
    @ApiResponse(responseCode = "200", description = "Course updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/me/id/{id}")
    public ResponseEntity<CourseDTO> updateMyCourse(@RequestBody @NotNull @Valid CourseDTO courseDTO)
    {
        log.info("updateCourse");
        CourseDTO courseDTOUpdated = courseService.updateCourse(
                courseDTO.getId(),
                courseDTO.getName(),
                courseDTO.getDescription(),
                courseDTO.getPrice()
        );
        return ResponseEntity.ok(courseDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Add content to course")
    @ApiResponse(responseCode = "200", description = "Content added")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PutMapping("/id/{id}/content")
    public ResponseEntity<CourseDTO> addContent(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable @NotNull Integer id,

            @Parameter(description = "Content entity to add")
            @RequestParam @NotNull ContentDTO content) {
        log.info("addContent to course with id {}", id);
        CourseDTO courseDTOUpdated = courseService.addContent(id, content);
        return ResponseEntity.ok(courseDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Add discount to course")
    @ApiResponse(responseCode = "200", description = "Discount applied, returns new price")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PutMapping("/id/{id}/discount")
    public ResponseEntity<Float> addDiscount(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable @NotNull Integer id,

            @Parameter(description = "Discount percentage (0.0 to 1.0)", example = "0.2")
            @RequestParam @NotNull Float discountPercentage) {
        log.info("addDiscount to course with id {}", id);
        Float priceWithDiscount = courseService.addDiscount(id, discountPercentage);
        return ResponseEntity.ok(priceWithDiscount);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE) or hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete course")
    @ApiResponse(responseCode = "204", description = "Course deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCourse with id {}", id);
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}