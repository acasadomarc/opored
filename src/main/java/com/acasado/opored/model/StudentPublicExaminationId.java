package com.acasado.opored.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;



@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StudentPublicExaminationId {
    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "public_examination_id", nullable = false)
    private Integer publicExaminationId;
}