package com.acasado.opored.dto;

import com.acasado.opored.model.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSummaryDTO {
    private Integer id;
    private String alias;
    private String profilePhoto;

    public UserSummaryDTO(UserEntity user) {
        setId(user.getId());
        setAlias(user.getAlias());
        setProfilePhoto(user.getProfilePhoto());
    }
}
