package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fields that an administrator can update on their own profile")
public class UserUpdateRequest {

    @Schema(description = "New first name (optional)", example = "Antonio")
    private String name;

    @Schema(description = "New surname (optional)", example = "Casado")
    private String surname;

    @Schema(description = "New alias (optional)", example = "antonio09")
    private String alias;

    @Schema(description = "New password (optional)", example = "N3wP@ss!")
    private String password;

    @Schema(description = "New profile photo (optional)", example = "/route/to/photo")
    private String profilePhoto;
}
