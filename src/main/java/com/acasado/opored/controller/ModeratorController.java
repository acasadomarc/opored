package com.acasado.opored.controller;

import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.service.ModeratorService;
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
        value = "/api/moderators",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Moderators", description = "Moderator management endpoints")
public class ModeratorController {

    private final ModeratorService moderatorService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all moderators")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<ModeratorDTO>> getAllModerators() {
        log.info("getAllModerators");
        List<ModeratorDTO> moderators = moderatorService.getAllModerators();
        return ResponseEntity.ok(moderators);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get moderator by ID")
    @ApiResponse(responseCode = "200", description = "Moderator found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ModeratorDTO> getModeratorById(
            @Parameter(description = "Moderator ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getModeratorById with id {}", id);
        ModeratorDTO moderatorDTO = moderatorService.getModeratorById(id);
        return ResponseEntity.ok(moderatorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get moderator by email")
    @ApiResponse(responseCode = "200", description = "Moderator found")
    @GetMapping("/email/{email}")
    public ResponseEntity<ModeratorDTO> getModeratorByEmail(
            @Parameter(description = "Moderator email", example = "moderator@example.com")
            @PathVariable @NotNull String email) {
        log.info("getModeratorByEmail with email {}", email);
        ModeratorDTO moderatorDTO = moderatorService.getModeratorByEmail(email);
        return ResponseEntity.ok(moderatorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_READ)")
    @Operation(summary = "Get current moderator")
    @ApiResponse(responseCode = "200", description = "Moderator returned")
    @GetMapping("/me")
    public ResponseEntity<ModeratorDTO> getMe() {
        log.info("getMe");
        ModeratorDTO moderatorDTO = moderatorService.getMe();
        return ResponseEntity.ok(moderatorDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_CREATE)")
    @Operation(summary = "Create moderator")
    @ApiResponse(responseCode = "201", description = "Moderator created")
    @PostMapping
    public ResponseEntity<AuthResponse> createModerator(
            @RequestBody @NotNull @Valid ModeratorDTO moderatorDTO) {
        log.info("createModerator with id: {}", moderatorDTO.getId());
        AuthResponse authResponse = moderatorService.createModerator(moderatorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_UPDATE)")
    @Operation(summary = "Update my profile")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PutMapping("/me")
    public ResponseEntity<ModeratorDTO> updateMe(
            @RequestBody @NotNull @Valid UserUpdateRequest userUpdateRequest) {
        log.info("updateMe");
        ModeratorDTO moderatorDTOUpdated = moderatorService.updateMe(userUpdateRequest);
        return ResponseEntity.ok(moderatorDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Delete moderator by ID")
    @ApiResponse(responseCode = "204", description = "Moderator deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModerator(
            @Parameter(description = "Moderator ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteModerator with id {}", id);
        moderatorService.disableModerator(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.MODERATION_DELETE)")
    @Operation(summary = "Delete my account")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe() {
        log.info("deleteMe");
        moderatorService.deleteMe();
        return ResponseEntity.noContent().build();
    }
}