package com.acasado.opored.service;

import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModeratorService moderatorService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        // Admins are not treated as normal users
        List<UserDTO> users = new LinkedList<>();

        for (UserEntity userEntity : userRepository.findAll()) {
            RoleEnum userRole = userEntity.getRole().getName();
            if (userRole != RoleEnum.ADMIN && userRole != RoleEnum.SUPER_ADMIN) {
                users.add(convertToUserDTO(userEntity));
            }
        }
        return users;
    }

    public void disableUser(Integer id) {
        UserEntity toDisableUser = userRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        switch (toDisableUser.getRole().getName()) {
            case MODERATOR -> moderatorService.disableModerator(id);
            case STUDENT -> studentService.disableStudent(id);
            case PROFESSOR -> professorService.disableProfessor(id);
        }
    }

    public void enableUser(Integer id) {
        UserEntity toEnableUser = userRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        switch (toEnableUser.getRole().getName()) {
            case MODERATOR -> moderatorService.enableModerator(id);
            case STUDENT -> studentService.enableStudent(id);
            case PROFESSOR -> professorService.enableProfessor(id);
        }
    }



    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("User with id %d not found", id));
    }

    private UserDTO convertToUserDTO(UserEntity user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .alias(user.getAlias())
                .email(user.getEmail())
                .password(user.getPassword())
                .registrationDate(user.getRegistrationDate())
                .profilePhoto(user.getProfilePhoto())
                .role(user.getRole().getName().toString())
                .isEnabled(user.isEnabled())
                .accountNoExpired(user.isAccountNoExpired())
                .accountNoLocked(user.isAccountNoLocked())
                .credentialNoExpired(user.isCredentialNoExpired())
                .build();
    }

}
