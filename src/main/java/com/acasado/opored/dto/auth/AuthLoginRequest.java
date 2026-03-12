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
@Schema(description = "User credentials for authentication")
public class AuthLoginRequest {

    @Schema(description = "User email or username", example = "user@example.com")
    @NotNull
    private String username;

    @Schema(description = "User password", example = "SecureP@ss123")
    @NotNull
    private String password;
}