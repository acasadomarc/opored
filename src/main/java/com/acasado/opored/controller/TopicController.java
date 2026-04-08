package com.acasado.opored.controller;

import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.dto.TopicDTO;
import com.acasado.opored.service.TopicService;
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
        value = "/api/topics",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Topics", description = "Topic management endpoints")
public class TopicController {

    private final TopicService topicService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all topics")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        log.info("getAllTopics");
        List<TopicDTO> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get topic by ID")
    @ApiResponse(responseCode = "200", description = "Topic found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopicById(
            @Parameter(description = "Topic ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getTopicById with id {}", id);
        TopicDTO topicDTO = topicService.getTopicById(id);
        return ResponseEntity.ok(topicDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_CREATE)")
    @Operation(summary = "Create a new topic")
    @ApiResponse(responseCode = "201", description = "Topic created")
    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(
            @RequestBody @NotNull @Valid TopicDTO topicDTO) {
        log.info("createTopic with id: {}", topicDTO.getId());
        TopicDTO topicDTOCreated = topicService.createTopic(topicDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(topicDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_UPDATE)")
    @Operation(summary = "Update my topic")
    @ApiResponse(responseCode = "200", description = "Topic updated")
    @ApiResponse(responseCode = "404", description = "Not found or not authorized")
    @PutMapping("/me/id/{id}")
    public ResponseEntity<TopicDTO> updateMyTopic(@PathVariable @NotNull Integer id, @RequestBody @NotNull @Valid TopicDTO topicDTO)
    {
        log.info("updateTopic");
        TopicDTO topicDTOUpdated = topicService.updateMyTopic(
                id,
                topicDTO.getTitle(),
                topicDTO.getStatus()
        );
        return ResponseEntity.ok(topicDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete topic (Admin)")
    @ApiResponse(responseCode = "204", description = "Topic deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTopic(
            @Parameter(description = "Topic ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteTopic with id {}", id);
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get students following a topic")
    @ApiResponse(responseCode = "200", description = "List of students returned")
    @GetMapping("/followingTopic/{id}")
    public ResponseEntity<Set<StudentSummaryDTO>> getFollowingStudents(
            @Parameter(description = "Topic ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getFollowedTopics by student with id {}", id);
        return ResponseEntity.ok(topicService.getStudentsFollowing(id));
    }
}