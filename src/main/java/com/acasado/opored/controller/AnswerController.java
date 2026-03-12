package com.acasado.opored.controller;

import com.acasado.opored.dto.AnswerDTO;
import com.acasado.opored.service.AnswerService;
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
        value = "/api/answers",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Answers", description = "Answer management endpoints")
public class AnswerController {

    private final AnswerService answerService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all answers")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<AnswerDTO>> getAllAnswers() {
        log.info("getAllAnswers");
        List<AnswerDTO> answers = answerService.getAllAnswers();
        return ResponseEntity.ok(answers);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new answer")
    @ApiResponse(responseCode = "201", description = "Answer created")
    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(
            @RequestBody @NotNull @Valid AnswerDTO answerDTO) {
        log.info("createAnswer");
        AnswerDTO answerDTOCreated = answerService.createAnswer(answerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(answerDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update an answer")
    @ApiResponse(responseCode = "200", description = "Answer updated")
    @ApiResponse(responseCode = "404", description = "Answer not found")
    @PutMapping("/me")
    public ResponseEntity<AnswerDTO> updateMyAnswer(
            @RequestBody @NotNull @Valid AnswerDTO answerDTO) {
        log.info("updateAnswer");
        AnswerDTO answerDTOUpdated = answerService.updateAnswer(answerDTO);
        return ResponseEntity.ok(answerDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE)")
    @Operation(summary = "Delete answer")
    @ApiResponse(responseCode = "204", description = "Answer deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnswer(
            @Parameter(description = "Answer ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCourse with id {}", id);
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}