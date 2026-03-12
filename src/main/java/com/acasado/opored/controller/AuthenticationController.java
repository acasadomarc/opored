package com.acasado.opored.controller;

import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.dto.auth.RefreshData;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/auth",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthenticationController {

    private final JpaUserDetailsService userDetailsService;

    @Operation(summary = "Sign up a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(
            @RequestBody @NotNull @Valid AuthCreateUserRequest authCreateUserRequest) {
        log.info("User signed up");
        AuthResponse authResponse = userDetailsService.createUser(authCreateUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @NotNull @Valid AuthLoginRequest userRequest) {
        return new ResponseEntity<>(userDetailsService.loginUser(userRequest), HttpStatus.OK);
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshData refreshData) {
        return new ResponseEntity<>(userDetailsService.refreshToken(refreshData), HttpStatus.OK);
    }

    @Operation(summary = "Delete stored token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshData refreshData) {
        userDetailsService.logout(refreshData);
        return ResponseEntity.noContent().build();
    }
}