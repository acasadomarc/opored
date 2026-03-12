package com.acasado.opored.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for creating a new user")
public class AuthCreateUserRequest {

    @Schema(description = "User first name", example = "Antonio")
    @NotNull
    private String name;

    @Schema(description = "User last name", example = "Casado")
    @NotNull
    private String surname;

    @Schema(description = "User alias", example = "antonio09")
    @NotBlank
    private String alias;

    @Schema(description = "User email address", example = "antonio@example.com")
    @NotNull
    private String email;

    @Schema(description = "User password", example = "SecureP@ss123!")
    @NotNull
    private String password;

    @Schema(description = "Role assigned to the new user", example = "STUDENT")
    @NotNull
    private String role;
}