package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "administrators")
public class AdministratorEntity extends UserEntity {

    public AdministratorEntity(UserIdentificationFields userIdentificationFields, UserAccountStatus accountStatus,  RoleEntity role) {
        super(userIdentificationFields, accountStatus, role);
    }
}
