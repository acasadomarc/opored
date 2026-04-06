package com.acasado.opored.controller;

import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/users",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_READ)")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("getAllUsers");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get current user")
    @ApiResponse(responseCode = "200", description = "User returned")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        log.info("getMe");
        return ResponseEntity.ok(userService.getMe());
    }

    @PreAuthorize("hasAuthority(@authorities.USER_UPDATE)")
    @Operation(summary = "Update my profile")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMe(
            @RequestBody @NotNull @Valid UserUpdateRequest userUpdateRequest) {
        log.info("updateMe");
        UserDTO userDTOUpdated = userService.updateMe(userUpdateRequest);
        return ResponseEntity.ok(userDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Disable user")
    @ApiResponse(responseCode = "204", description = "User disabled")
    @PutMapping("/disable/{id}")
    public ResponseEntity<Void> disableUser(@PathVariable @NotNull Integer id) {
        log.info("disableUser");
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Enable user")
    @ApiResponse(responseCode = "204", description = "User enabled")
    @PutMapping("/enable/{id}")
    public ResponseEntity<Void> enableUser(@PathVariable @NotNull Integer id) {
        log.info("enableUser");
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(@authorities.USER_DELETE)")
    @Operation(summary = "Delete my account")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe() {
        log.info("deleteMe");
        userService.deleteMe();
        return ResponseEntity.noContent().build();
    }

}
