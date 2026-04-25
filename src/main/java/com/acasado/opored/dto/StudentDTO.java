package com.acasado.opored.dto;

import com.acasado.opored.model.StudentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Complete student data")
public class StudentDTO extends UserDTO {

    @Schema(description = "List of public examinations the student is signed up for")
    private Set<PublicExaminationDTO> publicExaminations;

    public StudentDTO(StudentEntity student) {
        setId(student.getId());
        setName(student.getName());
        setSurname(student.getSurname());
        setAlias(student.getAlias());
        setEmail(student.getEmail());
        setPassword(student.getPassword());
        setRegistrationDate(student.getRegistrationDate());
        setProfilePhoto(student.getProfilePhoto());
        setEnabled(student.isEnabled());
        setAccountNoExpired(student.isAccountNoExpired());
        setAccountNoLocked(student.isAccountNoLocked());
        setCredentialNoExpired(student.isCredentialNoExpired());
        setPublicExaminations(student.getPublicExaminations().stream().map(PublicExaminationDTO::new).collect(Collectors.toSet()));
    }
}