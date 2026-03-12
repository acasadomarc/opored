package com.acasado.opored.controller;

import com.acasado.opored.dto.MessageDTO;
import com.acasado.opored.service.MessageService;
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
        value = "/api/messages",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Messages", description = "Message management endpoints")
public class MessageController {

    private final MessageService messageService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all messages")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        log.info("getAllMessages");
        List<MessageDTO> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get message by ID")
    @ApiResponse(responseCode = "200", description = "Message found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(
            @Parameter(description = "Message ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getMessageById with id {}", id);
        MessageDTO messageDTO = messageService.getMessageById(id);
        return ResponseEntity.ok(messageDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_CREATE)")
    @Operation(summary = "Create a new message")
    @ApiResponse(responseCode = "201", description = "Message created")
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(
            @RequestBody @NotNull @Valid MessageDTO messageDTO) {
        log.info("createMessage with id: {}", messageDTO.getId());
        MessageDTO messageDTOCreated = messageService.createMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_UPDATE)")
    @Operation(summary = "Update my message")
    @ApiResponse(responseCode = "200", description = "Message updated")
    @ApiResponse(responseCode = "404", description = "Not found or unauthorized")
    @PutMapping("/me/id/{id}")
    public ResponseEntity<MessageDTO> updateMyMessage(@RequestBody @NotNull @Valid MessageDTO messageDTO)
    {
        log.info("updateMessage");
        MessageDTO messageDTOUpdated = messageService.updateMyMessage(
                messageDTO.getId(),
                messageDTO.getContent(),
                messageDTO.getStatus()
        );
        return ResponseEntity.ok(messageDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_DELETE)")
    @Operation(summary = "Delete message")
    @ApiResponse(responseCode = "204", description = "Message deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(
            @Parameter(description = "Message ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteMessage with id {}", id);
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}