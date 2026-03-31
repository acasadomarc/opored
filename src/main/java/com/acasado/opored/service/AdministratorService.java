package com.acasado.opored.service;

import com.acasado.opored.dto.AdministratorDTO;
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
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    private final JpaUserDetailsService userDetailsService;

    public List<AdministratorDTO> getAllAdministrators() {
        return administratorRepository.findAll().stream().map(this::convertToAdministratorDTO).toList();
    }

    public AdministratorDTO getAdministratorById(Integer id) {
        AdministratorEntity administrator = administratorRepository.findById(id).orElseThrow(() -> notFoundById(id));

        return convertToAdministratorDTO(administrator);
    }

    public AdministratorDTO getAdministratorByEmail(String email) {
        AdministratorEntity administrator = administratorRepository.findByEmail(email).orElseThrow(() -> notFoundByEmail(email));

        return convertToAdministratorDTO(administrator);
    }

    public AuthResponse createAdministrator(AdministratorDTO administratorDTO) {
        AuthCreateUserRequest authCreateUserRequest = new AuthCreateUserRequest(administratorDTO.getName(),
                administratorDTO.getSurname(),
                administratorDTO.getAlias(),
                administratorDTO.getEmail(),
                administratorDTO.getPassword(),
                RoleEnum.ADMIN.toString());

        return userDetailsService.createUser(authCreateUserRequest);
    }

    public void deleteAdministrator(Integer id) {
        AdministratorEntity toDeleteAdministrator = administratorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteAdministrator.setIsDeleted(true);
        toDeleteAdministrator.setEnabled(false);
        administratorRepository.save(toDeleteAdministrator);
    }

    public void deleteMe() {
        Integer currentId = getCurrentAdministratorUserId();
        AdministratorEntity toDeleteAdministrator = administratorRepository.findById(currentId)
                .orElseThrow(() -> notFoundById(currentId));

        // Logical delete
        toDeleteAdministrator.setIsDeleted(true);
        toDeleteAdministrator.setEnabled(false);
        administratorRepository.save(toDeleteAdministrator);
    }

    public void deleteMe(AdministratorEntity toDeleteAdministrator) {
        toDeleteAdministrator.setIsDeleted(true);
        toDeleteAdministrator.setEnabled(false);
        administratorRepository.save(toDeleteAdministrator);
    }

    private AdministratorDTO convertToAdministratorDTO(AdministratorEntity administrator) {
        return AdministratorDTO.builder()
                .id(administrator.getId())
                .name(administrator.getName())
                .surname(administrator.getSurname())
                .alias(administrator.getAlias())
                .email(administrator.getEmail())
                .password(administrator.getPassword())
                .registrationDate(administrator.getRegistrationDate())
                .profilePhoto(administrator.getProfilePhoto())
                .isEnabled(administrator.isEnabled())
                .accountNoExpired(administrator.isAccountNoExpired())
                .accountNoLocked(administrator.isAccountNoLocked())
                .credentialNoExpired(administrator.isCredentialNoExpired())
                .build();
    }

    private Integer getCurrentAdministratorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Administrator with id %d not found", id));
    }

    private EntityNotFoundException notFoundByEmail(String email) {
        return new EntityNotFoundException(String.format("Administrator with email %s not found", email));
    }
}
