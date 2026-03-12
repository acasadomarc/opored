package com.acasado.opored.controller;

import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.service.QuestionService;
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
        value = "/api/questions",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Questions", description = "Question management endpoints")
public class QuestionController {

    private final QuestionService questionService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all questions")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        log.info("getAllQuestions");
        List<QuestionDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new question")
    @ApiResponse(responseCode = "201", description = "Question created")
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(
            @RequestBody @NotNull @Valid QuestionDTO questionDTO) {
        log.info("createQuestion");
        QuestionDTO questionDTOCreated = questionService.createQuestion(questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update a question")
    @ApiResponse(responseCode = "200", description = "Question updated")
    @ApiResponse(responseCode = "404", description = "Question not found")
    @PutMapping("/me")
    public ResponseEntity<QuestionDTO> updateMyQuestion(
            @RequestBody @NotNull @Valid QuestionDTO questionDTO) {
        log.info("updateQuestion");
        QuestionDTO questionDTOUpdated = questionService.updateQuestion(questionDTO);
        return ResponseEntity.ok(questionDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE)")
    @Operation(summary = "Delete question")
    @ApiResponse(responseCode = "204", description = "Question deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @Parameter(description = "Question ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCourse with id {}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}