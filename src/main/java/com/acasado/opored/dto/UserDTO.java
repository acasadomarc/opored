package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "User data")
public class UserDTO {
    @Schema(example = "1")
    @NotNull
    private Integer id;

    @Schema(example = "Antonio")
    @NotBlank
    private String name;

    @Schema(example = "Casado")
    @NotBlank
    private String surname;

    @Schema(example = "antonio09")
    @NotBlank
    private String alias;

    @Schema(example = "antonio@example.com")
    @NotBlank
    private String email;

    @Schema(example = "S3cret!23")
    @NotBlank
    private String password;

    @Schema(example = "2026-01-01")
    private LocalDate registrationDate;

    @Schema(example = "/route/to/photo")
    private String profilePhoto;

    @Schema(example = "STUDENT")
    private String role;

    private boolean isEnabled;
    private boolean accountNoExpired;
    private boolean accountNoLocked;
    private boolean credentialNoExpired;
}
