package com.acasado.opored.service;

import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;

    private final JpaUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public List<ModeratorDTO> getAllModerators() {
        return moderatorRepository.findAll().stream().map(this::convertToModeratorDTO).toList();
    }

    public ModeratorDTO getModeratorById(Integer id) {
        ModeratorEntity moderator = moderatorRepository.findById(id).orElseThrow(() -> notFoundById(id));

        return convertToModeratorDTO(moderator);
    }

    public ModeratorDTO getModeratorByEmail(String email) {
        ModeratorEntity moderator = moderatorRepository.findByEmail(email).orElseThrow(() -> notFoundByEmail(email));

        return convertToModeratorDTO(moderator);
    }

    public ModeratorDTO getMe() {
        Integer currentId = getCurrentModeratorUserId();
        ModeratorEntity moderator = moderatorRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        return convertToModeratorDTO(moderator);
    }

    public AuthResponse createModerator(ModeratorDTO moderatorDTO) {
        AuthCreateUserRequest authCreateUserRequest = new AuthCreateUserRequest(moderatorDTO.getName(),
                moderatorDTO.getSurname(),
                moderatorDTO.getAlias(),
                moderatorDTO.getEmail(),
                moderatorDTO.getPassword(),
                RoleEnum.MODERATOR.toString());

        return userDetailsService.createUser(authCreateUserRequest);
    }

    public ModeratorDTO updateMe(UserUpdateRequest userUpdateRequest) {
        Integer currentId = getCurrentModeratorUserId();

        ModeratorEntity toUpdateModerator = moderatorRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        if (moderatorRepository.findByAlias(userUpdateRequest.getAlias()).isPresent()) {
            throw new AliasAlreadyRegisteredException("User with alias " + userUpdateRequest.getAlias() + " already exists");
        }
        // Password validation
        if (!SecurityUtils.passwordValidation(userUpdateRequest.getPassword())) {
            throw new BadCredentialsException("Password is not valid");
        }

        toUpdateModerator.setName(userUpdateRequest.getName());
        toUpdateModerator.setSurname(userUpdateRequest.getSurname());
        toUpdateModerator.setAlias(userUpdateRequest.getAlias());
        toUpdateModerator.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        toUpdateModerator.setProfilePhoto(userUpdateRequest.getProfilePhoto());

        ModeratorEntity updatedModerator = moderatorRepository.save(toUpdateModerator);
        return convertToModeratorDTO(updatedModerator);
    }

    public void disableModerator(Integer id) {
        ModeratorEntity toDeleteModerator = moderatorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));
        // Logical delete
        toDeleteModerator.setEnabled(false);
        moderatorRepository.save(toDeleteModerator);
    }

    public void enableModerator(Integer id) {
        ModeratorEntity toDeleteModerator = moderatorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));
        // Logical delete
        toDeleteModerator.setEnabled(true);
        moderatorRepository.save(toDeleteModerator);
    }

    public void deleteMe() {
        Integer currentId = getCurrentModeratorUserId();
        ModeratorEntity toDeleteModerator = moderatorRepository.findById(currentId)
                .orElseThrow(() -> notFoundById(currentId));

        // Logical delete
        toDeleteModerator.setIsDeleted(true);
        toDeleteModerator.setEnabled(false);
        moderatorRepository.save(toDeleteModerator);
    }

    private ModeratorDTO convertToModeratorDTO(ModeratorEntity moderator) {
        return ModeratorDTO.builder()
                .id(moderator.getId())
                .name(moderator.getName())
                .surname(moderator.getSurname())
                .alias(moderator.getAlias())
                .email(moderator.getEmail())
                .password(moderator.getPassword())
                .registrationDate(moderator.getRegistrationDate())
                .profilePhoto(moderator.getProfilePhoto())
                .isEnabled(moderator.isEnabled())
                .accountNoExpired(moderator.isAccountNoExpired())
                .accountNoLocked(moderator.isAccountNoLocked())
                .credentialNoExpired(moderator.isCredentialNoExpired())
                .build();
    }

    private Integer getCurrentModeratorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Moderator with id %d not found", id));
    }

    private EntityNotFoundException notFoundByEmail(String email) {
        return new EntityNotFoundException(String.format("Moderator with email %s not found", email));
    }
}
