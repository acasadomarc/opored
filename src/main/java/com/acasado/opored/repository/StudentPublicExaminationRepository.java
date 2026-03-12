package com.acasado.opored.repository;

import com.acasado.opored.model.StudentPublicExamination;
import com.acasado.opored.model.StudentPublicExaminationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPublicExaminationRepository extends JpaRepository<StudentPublicExamination, StudentPublicExaminationId> {
}
