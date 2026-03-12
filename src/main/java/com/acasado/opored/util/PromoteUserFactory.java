package com.acasado.opored.util;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PromoteUserFactory {

    public static RoleEntity createRole(RoleEnum roleEnum) {
        RoleEntity role = new RoleEntity();
        role.setName(roleEnum);
        return role;
    }

    public static StudentEntity createStudentEntity() {
        StudentEntity student = new StudentEntity();
        student.setId(1);
        student.setEmail("student@example.com");
        student.setRole(createRole(RoleEnum.STUDENT));
        return student;
    }

    public static ModeratorEntity createModeratorEntity() {
        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setId(2);
        moderator.setEmail("mod@example.com");
        moderator.setRole(createRole(RoleEnum.MODERATOR));
        return moderator;
    }

    public static AdministratorEntity createAdministratorEntity() {
        AdministratorEntity admin = new AdministratorEntity();
        admin.setId(3);
        admin.setEmail("admin@example.com");
        admin.setRole(createRole(RoleEnum.ADMIN));
        return admin;
    }
}