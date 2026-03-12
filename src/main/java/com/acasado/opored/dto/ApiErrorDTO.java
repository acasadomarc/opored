package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Standard API error response")
public class ApiErrorDTO {
    @Schema(example = "403")
    private int status;

    @Schema(example = "FORBIDDEN")
    private String error;

    @Schema(example = "You do not have permission")
    private String message;

    @Schema(example = "2026-01-31T12:34:56Z")
    private String timestamp;
}
