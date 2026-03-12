package com.acasado.opored.controller;

import com.acasado.opored.dto.AdministratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.service.AdministratorService;
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
        value = "/api/administrators",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrators", description = "Administrator management endpoints")
public class AdministratorController {

    private final AdministratorService administratorService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all administrators")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<AdministratorDTO>> getAllAdministrators() {
        log.info("getAllAdministrators");
        return ResponseEntity.ok(administratorService.getAllAdministrators());
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get administrator by ID")
    @ApiResponse(responseCode = "200", description = "Administrator found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<AdministratorDTO> getAdministratorById(
            @Parameter(description = "Administrator ID", example = "1")
            @PathVariable @NotNull Integer id) {

        log.info("getAdministratorById with id {}", id);
        return ResponseEntity.ok(administratorService.getAdministratorById(id));
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get current administrator")
    @ApiResponse(responseCode = "200", description = "Administrator returned")
    @GetMapping("/me")
    public ResponseEntity<AdministratorDTO> getMe() {
        log.info("getMe");
        return ResponseEntity.ok(administratorService.getMe());
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get administrator by email")
    @ApiResponse(responseCode = "200", description = "Administrator found")
    @GetMapping("/email/{email}")
    public ResponseEntity<AdministratorDTO> getAdministratorByEmail(
            @Parameter(description = "Administrator email", example = "antonio@example.com")
            @PathVariable @NotNull String email) {

        log.info("getAdministratorByEmail with email {}", email);
        return ResponseEntity.ok(administratorService.getAdministratorByEmail(email));
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Create administrator")
    @ApiResponse(responseCode = "201", description = "Administrator created")
    @PostMapping
    public ResponseEntity<AuthResponse> createAdministrator(
            @RequestBody @NotNull @Valid AdministratorDTO administratorDTO) {

        log.info("createAdministrator with id: {}", administratorDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(administratorService.createAdministrator(administratorDTO));
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update my profile")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PutMapping("/me")
    public ResponseEntity<AdministratorDTO> updateMe(
            @RequestBody @NotNull @Valid UserUpdateRequest userUpdateRequest) {

        log.info("updateMe");
        return ResponseEntity.ok(administratorService.updateMe(userUpdateRequest));
    }

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Delete administrator")
    @ApiResponse(responseCode = "204", description = "Administrator deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrator(@PathVariable @NotNull Integer id) {
        log.info("deleteAdministrator with id {}", id);
        administratorService.deleteAdministrator(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete my account")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe() {
        log.info("deleteMe");
        administratorService.deleteMe();
        return ResponseEntity.noContent().build();
    }
}
