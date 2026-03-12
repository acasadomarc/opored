package com.acasado.opored.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication response containing the access token")
public class AuthResponse {

    @Schema(description = "Authenticated username", example = "user@example.com")
    @NotNull
    private String username;

    @Schema(description = "Response message", example = "Login successful")
    @NotNull
    private String message;

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikp...")
    @NotNull
    private String accessToken;

    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikp...")
    @NotNull
    private String refreshToken;

    @Schema(description = "Operation status", example = "true")
    @NotNull
    private int status;
}