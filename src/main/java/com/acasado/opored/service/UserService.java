package com.acasado.opored.service;

import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.UserRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModeratorService moderatorService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final AdministratorService administratorService;
    private final TopicService topicService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final StorageService storageService;

    private final PasswordEncoder passwordEncoder;

    private static final Integer DEFAULT_DELETED_USER_ID = 1;

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

    public UserDTO getMe() {
        Integer currentId = getCurrentUserId();
        UserEntity user = userRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return convertToUserDTO(user);
    }

    public void disableUser(Integer id) {
        UserEntity toDisableUser = userRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        switch (toDisableUser.getRole().getName()) {
            case MODERATOR -> moderatorService.disableModerator(id);
            case PROFESSOR -> professorService.disableProfessor(id);
            default -> studentService.disableStudent(id);
        }
    }

    public UserDTO updateMe(UserUpdateRequest userUpdateRequest) {
        Integer currentId = getCurrentUserId();

        UserEntity toUpdateUser = userRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        // Alias validation

        if (!validateAlias(userUpdateRequest.getAlias(), toUpdateUser.getAlias(), toUpdateUser.getRole().getName())) {
            throw new BadCredentialsException("Alias is not valid");
        }


        toUpdateUser.setName(userUpdateRequest.getName());
        toUpdateUser.setSurname(userUpdateRequest.getSurname());
        toUpdateUser.setAlias(userUpdateRequest.getAlias());
        // Password validation
        if (!Objects.equals(userUpdateRequest.getPassword(), "")) {
            if (!SecurityUtils.passwordValidation(userUpdateRequest.getPassword())) {
                throw new BadCredentialsException("Password is not valid");
            } else {
                // We only update the password if a new one is sent
                toUpdateUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
            }
        }
        // Remove the previous profile photo from the filesystem
        if (toUpdateUser.getProfilePhoto() != null && !toUpdateUser.getProfilePhoto().equals(userUpdateRequest.getProfilePhoto())) {
            storageService.delete(toUpdateUser.getProfilePhoto());
            toUpdateUser.setProfilePhoto(userUpdateRequest.getProfilePhoto());
        }
        toUpdateUser.setProfilePhoto(userUpdateRequest.getProfilePhoto());

        UserEntity updatedUser = userRepository.save(toUpdateUser);
        return convertToUserDTO(updatedUser);
    }

    public void enableUser(Integer id) {
        UserEntity toEnableUser = userRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        switch (toEnableUser.getRole().getName()) {
            case MODERATOR -> moderatorService.enableModerator(id);
            case PROFESSOR -> professorService.enableProfessor(id);
            default -> studentService.enableStudent(id);
        }
    }

    @Transactional // Avoid inconsistency due to uncompleted operations
    public void deleteMe() {
        Integer currentId = getCurrentUserId();
        UserEntity toDeleteUser = userRepository.findById(currentId)
                .orElseThrow(() -> notFoundById(currentId));

        // Clean the actual tokens
        toDeleteUser.getRefreshTokens().clear();

        // User to reference in the existent topics and messages
        UserEntity defaultDeletedUser = userRepository.findById(DEFAULT_DELETED_USER_ID).orElseThrow(() -> notFoundById(DEFAULT_DELETED_USER_ID));

        if (!toDeleteUser.getMessages().isEmpty()) {
            messageService.changeMessagesOwner(toDeleteUser.getMessages(), defaultDeletedUser);
        }

        if (!toDeleteUser.getTopics().isEmpty()) {
            topicService.changeTopicsOwner(toDeleteUser.getTopics(), defaultDeletedUser);
        }

        switch (toDeleteUser.getRole().getName()) {
            case MODERATOR -> moderatorService.deleteMe((ModeratorEntity) toDeleteUser);
            case PROFESSOR -> professorService.deleteMe((ProfessorEntity) toDeleteUser);
            case ADMIN -> administratorService.deleteMe((AdministratorEntity) toDeleteUser);
            default -> studentService.deleteMe((StudentEntity) toDeleteUser);
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

    private Integer getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean validateAlias(String newAlias, String currentAlias, RoleEnum userRole) {
        if (!newAlias.equals(currentAlias) && userRepository.findByAlias(newAlias).isPresent()) {
            throw new AliasAlreadyRegisteredException("User with alias " + newAlias + " already exists");

        }
        if (userRole == RoleEnum.STUDENT || userRole == RoleEnum.PROFESSOR) {
            return SecurityUtils.publicUserAliasValidation(newAlias);
        }
        else {
            return SecurityUtils.privilegedUserAliasValidation(newAlias);
        }
    }

}
