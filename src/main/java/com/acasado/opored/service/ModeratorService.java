package com.acasado.opored.service;

import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final ModerationMessageService moderationMessageService;
    private final ModerationTopicService moderationTopicService;

    private final JpaUserDetailsService userDetailsService;

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

    public AuthResponse createModerator(ModeratorDTO moderatorDTO) {
        AuthCreateUserRequest authCreateUserRequest = new AuthCreateUserRequest(moderatorDTO.getName(),
                moderatorDTO.getSurname(),
                moderatorDTO.getAlias(),
                moderatorDTO.getEmail(),
                moderatorDTO.getPassword(),
                RoleEnum.MODERATOR.toString());

        return userDetailsService.createPrivilegedUser(authCreateUserRequest);
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

    public void deleteMe(ModeratorEntity toDeleteModerator) {
        // When we delete a moderator, their moderations are sent to another moderator in the system.
        // If there are no available moderators, the method will throw an exception

        ModeratorEntity subModerator = moderatorRepository.findFirstByIdNot(toDeleteModerator.getId()).orElseThrow(() -> new IllegalStateException("There must exist at least one moderator in the system"));

        moderationMessageService.changeModerationMessagesOwner(toDeleteModerator.getModerationMessages(), subModerator);
        moderationTopicService.changeModerationTopicsOwner(toDeleteModerator.getModerationTopics(), subModerator);

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
