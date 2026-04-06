package com.acasado.opored.controller;

import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.service.QuizService;
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
        value = "/api/quizzes",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quizzes", description = "Quiz management endpoints")
public class QuizController {

    private final QuizService quizService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all Quizzes")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        log.info("getAllQuizzes");
        List<QuizDTO> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new Quiz")
    @ApiResponse(responseCode = "201", description = "Quiz created")
    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(
            @RequestBody @NotNull @Valid QuizDTO quizDTO) {
        log.info("createQuiz");
        QuizDTO quizDTOCreated = quizService.createQuiz(quizDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update a Quiz")
    @ApiResponse(responseCode = "200", description = "Quiz updated")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    @PutMapping("/me")
    public ResponseEntity<QuizDTO> updateMyQuiz(
            @RequestBody @NotNull @Valid QuizDTO quizDTO) {
        log.info("updateQuiz");
        QuizDTO quizDTOUpdated = quizService.updateQuiz(quizDTO);
        return ResponseEntity.ok(quizDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE)")
    @Operation(summary = "Delete Quiz")
    @ApiResponse(responseCode = "204", description = "Quiz deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(
            @Parameter(description = "Quiz ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteQuiz with id {}", id);
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}