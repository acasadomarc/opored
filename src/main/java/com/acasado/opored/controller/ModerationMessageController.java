package com.acasado.opored.controller;

import com.acasado.opored.dto.ModerationMessageDTO;
import com.acasado.opored.service.ModerationMessageService;
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
        value = "/api/moderationMessages",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Moderation Messages", description = "Moderation message management endpoints")
public class ModerationMessageController {

    private final ModerationMessageService moderationMessageService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all moderated messages")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<ModerationMessageDTO>> getAllModeratedMessages() {
        log.info("getAllModeratedMessages");
        List<ModerationMessageDTO> moderatedMessages = moderationMessageService.getAllModeratedMessages();
        return ResponseEntity.ok(moderatedMessages);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_READ)")
    @Operation(summary = "Get my moderated messages")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping("/me")
    public ResponseEntity<List<ModerationMessageDTO>> getMyModeratedMessages() {
        log.info("getAllMyModeratedMessages");
        List<ModerationMessageDTO> moderatedMessages = moderationMessageService.getMyModeratedMessages();
        return ResponseEntity.ok(moderatedMessages);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_READ)")
    @Operation(summary = "Get moderated message by composite ID")
    @ApiResponse(responseCode = "200", description = "Moderation entry found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/message/{messageId}/moderator/{moderatorId}")
    public ResponseEntity<ModerationMessageDTO> getModeratedMessageById(
            @Parameter(description = "Message ID", example = "10")
            @PathVariable @NotNull Integer messageId,
            @Parameter(description = "Moderator ID", example = "5")
            @PathVariable @NotNull Integer moderatorId
    ) {
        log.info("getModeratedMessageById with id {}", messageId);
        ModerationMessageDTO moderatedMessage = moderationMessageService.getModerationMessageById(messageId, moderatorId);
        return ResponseEntity.ok(moderatedMessage);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_CREATE)")
    @Operation(summary = "Moderate a message")
    @ApiResponse(responseCode = "201", description = "Message moderated successfully")
    @PostMapping
    public ResponseEntity<ModerationMessageDTO> moderateMessage(
            @RequestBody @NotNull @Valid ModerationMessageDTO moderationMessageDTO
    ) {
        log.info("moderateMessage with id {}", moderationMessageDTO.getMessageId());
        ModerationMessageDTO moderationMessageDTOCreated = moderationMessageService.moderateMessage(moderationMessageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(moderationMessageDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_UPDATE)")
    @Operation(summary = "Update my moderation reason")
    @ApiResponse(responseCode = "200", description = "Moderation updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/me/message/{messageId}/")
    public ResponseEntity<ModerationMessageDTO> updateModeratedMessageByMe(@RequestBody @NotNull @Valid ModerationMessageDTO moderationMessageDTO)
    {
        log.info("updateMyModeratedTopic");
        ModerationMessageDTO moderationMessageDTOUpdated = moderationMessageService.updateModeratedMessageByMe(
                moderationMessageDTO.getMessageId(),
                moderationMessageDTO.getReason()
        );
        return ResponseEntity.ok(moderationMessageDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Delete moderation entry")
    @ApiResponse(responseCode = "200", description = "Moderation deleted")
    @DeleteMapping("/message/{messageId}/moderator/{moderatorId}")
    public ResponseEntity<String> deleteModerationMessage(
            @Parameter(description = "Message ID", example = "10")
            @PathVariable @NotNull Integer messageId,
            @Parameter(description = "Moderator ID", example = "5")
            @PathVariable @NotNull Integer moderatorId
    ) {
        log.info("deleteModerationMessage with id {}", messageId);
        moderationMessageService.deleteModerationMessage(messageId, moderatorId);
        return ResponseEntity.ok("Moderación de mensaje eliminada correctamente");
    }
}