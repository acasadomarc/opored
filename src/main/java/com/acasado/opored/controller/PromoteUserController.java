package com.acasado.opored.controller;

import com.acasado.opored.service.PromoteUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/api/promotions",
        produces = "text/plain")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Promotions", description = "User role promotion and demotion endpoints")
public class PromoteUserController {

    private final PromoteUserService promoteUserService;

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Promote student to moderator")
    @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Student not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "User is already a moderator", content = @Content)
    @PutMapping("/promoteModerator/{id}")
    public ResponseEntity<String> promoteToModerator(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("promoteToModerator student with id {}", id);
        promoteUserService.promoteToModerator(id);
        return ResponseEntity.ok("Role updated from student to moderator successfully");
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Demote moderator to student")
    @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Moderator not found", content = @Content)
    @PutMapping("/demoteModerator/{id}")
    public ResponseEntity<String> demoteFromModerator(
            @Parameter(description = "Moderator ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("demoteFromModerator moderator with id {}", id);
        promoteUserService.demoteFromModerator(id);
        return ResponseEntity.ok("Role updated from moderator to student successfully");
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Promote user to administrator")
    @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "User is already an administrator", content = @Content)
    @PutMapping("/promoteAdministrator/{id}/actualRole/{role}")
    public ResponseEntity<String> promoteToAdministrator(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @NotNull Integer id,

            @Parameter(description = "Current user role", example = "student")
            @PathVariable @NotNull String role) {
        log.info("promoteToAdministrator {} with id {}", role, id);
        promoteUserService.promoteToAdministrator(id, role);
        return ResponseEntity.ok("Role updated from " + role + " to administrator successfully");
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Demote administrator to another role")
    @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Administrator not found", content = @Content)
    @PutMapping("/demoteAdministrator/{id}/newRole/{role}")
    public ResponseEntity<String> demoteFromAdministrator(
            @Parameter(description = "Administrator ID", example = "1")
            @PathVariable @NotNull Integer id,

            @Parameter(description = "Target role (e.g., student, moderator)", example = "student")
            @PathVariable @NotNull String role) {
        log.info("demoteFromAdministrator administrator with id {} to {}", id, role);
        promoteUserService.demoteFromAdministrator(id, role);
        return ResponseEntity.ok("Role updated from administrator to " + role + " successfully");
    }
}