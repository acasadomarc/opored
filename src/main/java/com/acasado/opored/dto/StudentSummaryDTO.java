package com.acasado.opored.dto;

import com.acasado.opored.model.StudentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Brief student information")
public class StudentSummaryDTO extends UserDTO {
    public StudentSummaryDTO(StudentEntity student) {
        setId(student.getId());
        setName(student.getName());
        setSurname(student.getSurname());
        setAlias(student.getAlias());
        setEmail(student.getEmail());
        setRegistrationDate(student.getRegistrationDate());
        setProfilePhoto(student.getProfilePhoto());
        setEnabled(student.isEnabled());
        setAccountNoExpired(student.isAccountNoExpired());
        setAccountNoLocked(student.isAccountNoLocked());
        setCredentialNoExpired(student.isCredentialNoExpired());
    }
}