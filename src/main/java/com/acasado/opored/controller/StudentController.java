package com.acasado.opored.controller;

import com.acasado.opored.dto.*;

import com.acasado.opored.service.StudentService;
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
import java.util.Set;

@RestController
@RequestMapping(
        value = "/api/students",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Student management endpoints")
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get all students")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<StudentSummaryDTO>> getAllStudents() {
        log.info("getAllStudents");
        List<StudentSummaryDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get student by ID")
    @ApiResponse(responseCode = "200", description = "Student found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<StudentSummaryDTO> getStudentById(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getStudentById");
        StudentSummaryDTO studentDTO = studentService.getStudentById(id);
        return ResponseEntity.ok(studentDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get current student")
    @ApiResponse(responseCode = "200", description = "Student returned")
    @GetMapping("/me")
    public ResponseEntity<StudentDTO> getMe() {
        log.info("getMe");
        StudentDTO studentDTO = studentService.getMe();
        return ResponseEntity.ok(studentDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get student by email")
    @ApiResponse(responseCode = "200", description = "Student found")
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentSummaryDTO> getStudentByEmail(
            @Parameter(description = "Student email", example = "student@example.com")
            @PathVariable @NotNull String email) {
        log.info("getStudentByEmail");
        StudentSummaryDTO studentDTO = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(studentDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete student by ID")
    @ApiResponse(responseCode = "204", description = "Student deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteStudent");
        studentService.disableStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Student - Topic relation methods

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get followed topics")
    @ApiResponse(responseCode = "200", description = "Topics returned")
    @GetMapping("/me/topics")
    public ResponseEntity<Set<TopicSummaryDTO>> getFollowedTopics() {
        log.info("getFollowedTopics by student");
        return ResponseEntity.ok(studentService.getFollowedTopics());
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_CREATE)")
    @Operation(summary = "Follow a topic")
    @ApiResponse(responseCode = "200", description = "Topic followed successfully")
    @PostMapping("/me/topics/{topicId}")
    public ResponseEntity<String> followTopic(
            @Parameter(description = "Topic ID", example = "5")
            @PathVariable @NotNull Integer topicId) {
        log.info("followTopic with id {}", topicId);
        studentService.followTopic(topicId);
        return ResponseEntity.ok("Seguimiento establecido correctamente");
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_DELETE)")
    @Operation(summary = "Unfollow a topic")
    @ApiResponse(responseCode = "200", description = "Topic unfollowed successfully")
    @DeleteMapping("/me/topics/{topicId}")
    public ResponseEntity<String> unfollowTopic(
            @Parameter(description = "Topic ID", example = "5")
            @PathVariable @NotNull Integer topicId) {
        log.info("unfollowTopic with id {}", topicId);
        studentService.unfollowTopic(topicId);
        return ResponseEntity.ok("Seguimiento eliminado correctamente");
    }

    // Student - Public Examination relation methods

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get enrolled public examinations")
    @ApiResponse(responseCode = "200", description = "Public examinations returned")
    @GetMapping("/me/publicExaminations")
    public ResponseEntity<Set<PublicExaminationSummaryDTO>> getEnrolledPublicExaminations() {
        log.info("getEnrolledPublicExaminations by student");
        return ResponseEntity.ok(studentService.getEnrolledPublicExaminations());
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_CREATE)")
    @Operation(summary = "Sign up for a public examination")
    @ApiResponse(responseCode = "200", description = "Signed up successfully")
    @PostMapping("/me/publicExamination/{publicExaminationId}")
    public ResponseEntity<Void> signUpForPublicExamination(
            @Parameter(description = "Public Examination ID", example = "10")
            @PathVariable @NotNull Integer publicExaminationId) {
        log.info("signUpForPublicExamination");
        studentService.signUpForPublicExamination(publicExaminationId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_DELETE)")
    @Operation(summary = "Withdraw from a public examination")
    @ApiResponse(responseCode = "200", description = "Withdrawn successfully")
    @DeleteMapping("/me/publicExamination/{publicExaminationId}")
    public ResponseEntity<Void> withdrawFromPublicExamination(
            @Parameter(description = "Public Examination ID", example = "10")
            @PathVariable @NotNull Integer publicExaminationId) {
        log.info("withdrawFromPublicExamination");
        studentService.withdrawFromPublicExamination(publicExaminationId);
        return ResponseEntity.ok().build();
    }

    // Student - Course/Purchase relation methods

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get my courses")
    @ApiResponse(responseCode = "200", description = "Courses returned")
    @GetMapping("/me/courses")
    public ResponseEntity<Set<CourseDTO>> getCourses() {
        log.info("getCourses");
        return ResponseEntity.ok(studentService.getCourses());
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get my purchases")
    @ApiResponse(responseCode = "200", description = "Purchases returned")
    @GetMapping("/me/purchases")
    public ResponseEntity<Set<PurchaseDTO>> getPurchases() {
        log.info("getPurchases");
        return ResponseEntity.ok(studentService.getPurchases());
    }
}