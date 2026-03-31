package com.acasado.opored.util;

import com.acasado.opored.enumeration.PermissionEnum;
import org.springframework.stereotype.Component;

// Used to simplify the preAuthorize annotations
@Component("authorities")
@SuppressWarnings("java:S116") // Silence sonarQube warnings about field naming
public class PermissionExpression {

    public final String USER_READ = PermissionEnum.USER_READ.name();
    public final String USER_CREATE = PermissionEnum.USER_CREATE.name();
    public final String USER_UPDATE = PermissionEnum.USER_UPDATE.name();
    public final String USER_DELETE = PermissionEnum.USER_DELETE.name();
    public final String STUDENT_READ = PermissionEnum.STUDENT_READ.name();
    public final String STUDENT_CREATE = PermissionEnum.STUDENT_CREATE.name();
    public final String STUDENT_UPDATE = PermissionEnum.STUDENT_UPDATE.name();
    public final String STUDENT_DELETE = PermissionEnum.STUDENT_DELETE.name();
    public final String MODERATION_READ = PermissionEnum.MODERATION_READ.name();
    public final String MODERATION_CREATE = PermissionEnum.MODERATION_CREATE.name();
    public final String MODERATION_UPDATE = PermissionEnum.MODERATION_UPDATE.name();
    public final String MODERATION_DELETE = PermissionEnum.MODERATION_DELETE.name();
    public final String ADMINISTRATION_READ = PermissionEnum.ADMINISTRATION_READ.name();
    public final String ADMINISTRATION_CREATE = PermissionEnum.ADMINISTRATION_CREATE.name();
    public final String ADMINISTRATION_UPDATE = PermissionEnum.ADMINISTRATION_UPDATE.name();
    public final String ADMINISTRATION_DELETE = PermissionEnum.ADMINISTRATION_DELETE.name();
    public final String PROFESSOR_READ = PermissionEnum.PROFESSOR_READ.name();
    public final String PROFESSOR_CREATE = PermissionEnum.PROFESSOR_CREATE.name();
    public final String PROFESSOR_UPDATE = PermissionEnum.PROFESSOR_UPDATE.name();
    public final String PROFESSOR_DELETE = PermissionEnum.PROFESSOR_DELETE.name();
    public final String ROOT = PermissionEnum.ROOT.name();
}
