package com.acasado.opored.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentificationFields {
    private String name;
    private String surname;
    private String alias;
    private String email;
    private String password;
}