package com.acasado.opored.controller;

import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.service.ModerationTopicService;
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
        value = "/api/moderationTopics",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Moderation Topics", description = "Moderation topic management endpoints")
public class ModerationTopicController {

    private final ModerationTopicService moderationTopicService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all moderated topics")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<ModerationTopicDTO>> getAllModeratedTopics() {
        log.info("getAllModeratedTopics");
        List<ModerationTopicDTO> moderatedTopics = moderationTopicService.getAllModeratedTopics();
        return ResponseEntity.ok(moderatedTopics);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_READ)")
    @Operation(summary = "Get my moderated topics")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping("/me")
    public ResponseEntity<List<ModerationTopicDTO>> getMyModeratedTopics() {
        log.info("getAllMyModeratedTopics");
        List<ModerationTopicDTO> moderatedTopics = moderationTopicService.getMyModeratedTopics();
        return ResponseEntity.ok(moderatedTopics);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_READ)")
    @Operation(summary = "Get moderated topic by composite ID")
    @ApiResponse(responseCode = "200", description = "Moderation entry found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("topic/{topicId}/moderator/{moderatorId}")
    public ResponseEntity<ModerationTopicDTO> getModeratedTopicById(
            @Parameter(description = "Topic ID", example = "15")
            @PathVariable @NotNull Integer topicId,
            @Parameter(description = "Moderator ID", example = "3")
            @PathVariable @NotNull Integer moderatorId) {
        log.info("getMyModeratedTopicById  with id {}", topicId);
        ModerationTopicDTO moderatedTopic = moderationTopicService.getModerationTopicById(topicId, moderatorId);
        return ResponseEntity.ok(moderatedTopic);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_CREATE)")
    @Operation(summary = "Moderate a topic")
    @ApiResponse(responseCode = "201", description = "Topic moderated successfully")
    @PostMapping
    public ResponseEntity<ModerationTopicDTO> moderateTopic(
            @RequestBody @NotNull @Valid ModerationTopicDTO moderationTopicDTO
    ) {
        log.info("moderateTopic with id {}", moderationTopicDTO.getTopicId());
        ModerationTopicDTO moderationTopicDTOCreated = moderationTopicService.moderateTopic(moderationTopicDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(moderationTopicDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_UPDATE)")
    @Operation(summary = "Update my moderation reason")
    @ApiResponse(responseCode = "200", description = "Moderation updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/me/topic/{topicId}")
    public ResponseEntity<ModerationTopicDTO> updateModeratedTopicByMe(@RequestBody @NotNull @Valid ModerationTopicDTO moderationTopicDTO)
    {
        log.info("updateMyModeratedTopic");
        ModerationTopicDTO moderationTopicDTOUpdated = moderationTopicService.updateModeratedTopicByMe(
                moderationTopicDTO.getTopicId(),
                moderationTopicDTO.getReason()
        );
        return ResponseEntity.ok(moderationTopicDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Delete moderation entry")
    @ApiResponse(responseCode = "200", description = "Moderation deleted")
    @DeleteMapping("/topic/{topicId}/moderator/{moderatorId}")
    public ResponseEntity<String> deleteModerationTopic(
            @Parameter(description = "Topic ID", example = "15")
            @PathVariable @NotNull Integer topicId,
            @Parameter(description = "Moderator ID", example = "3")
            @PathVariable @NotNull Integer moderatorId
    ) {
        log.info("deleteModerationTopic with id {}", topicId);
        moderationTopicService.deleteModerationTopic(topicId, moderatorId);
        return ResponseEntity.ok("Moderación de tema eliminada correctamente");
    }
}