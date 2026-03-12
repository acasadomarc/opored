package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Administrator data")
public class AdministratorDTO extends UserDTO {

}
