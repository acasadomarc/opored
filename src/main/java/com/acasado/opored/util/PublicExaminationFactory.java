package com.acasado.opored.util;

import com.acasado.opored.dto.PublicExaminationDTO;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PublicExaminationFactory {

    public static PublicExaminationDTO createValidPublicExaminationDTO() {
        return new PublicExaminationDTO(
                1,
                "National Police",
                "Exams for police corps",
                5,  // CategoryId
                10, // BulletinBoardId
                20  // ForumId
        );
    }

    public static PublicExaminationDTO createInvalidPublicExaminationDTO() {
        return new PublicExaminationDTO(
                null,
                null,
                "Desc",
                null,
                null,
                null
        );
    }

    public static PublicExaminationEntity createValidPublicExaminationEntity() {
        CategoryEntity category = new CategoryEntity();
        category.setId(5);
        category.setName("Security");

        ForumEntity forum = new ForumEntity();
        forum.setId(20);

        BulletinBoardEntity bulletinBoard = new BulletinBoardEntity();
        bulletinBoard.setId(10);

        PublicExaminationEntity entity = new PublicExaminationEntity();
        entity.setId(1);
        entity.setName("National Police");
        entity.setDescription("Exams for police corps");
        entity.setCategory(category);
        entity.setForum(forum);
        entity.setBulletinBoard(bulletinBoard);
        entity.setIsDeleted(false);
        entity.setStudents(new HashSet<>());

        return entity;
    }

    // Helper to test getStudents()
    public static PublicExaminationEntity createPublicExaminationWithStudents() {
        PublicExaminationEntity entity = createValidPublicExaminationEntity();

        StudentEntity student = new StudentEntity();
        student.setId(100);
        student.setName("John");
        student.setEnabled(true);

        Set<StudentEntity> students = new HashSet<>();
        students.add(student);
        entity.setStudents(students);

        return entity;
    }
}