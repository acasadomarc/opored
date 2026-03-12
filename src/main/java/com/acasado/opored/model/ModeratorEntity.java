package com.acasado.opored.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "moderators")
public class ModeratorEntity extends UserEntity {
    public ModeratorEntity(String name, String surname, String alias, String email, String password, UserAccountStatus accountStatus, RoleEntity role) {
        super(name, surname, alias, email, password, accountStatus, role);
    }
}
