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

    public AdministratorEntity(String name, String surname, String alias, String email, String password, UserAccountStatus accountStatus, RoleEntity role) {
        super(name, surname, alias, email, password, accountStatus, role);
    }
}
