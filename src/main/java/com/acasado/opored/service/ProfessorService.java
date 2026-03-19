package com.acasado.opored.service;

import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    private final PasswordEncoder passwordEncoder;

    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll().stream().map(this::convertToProfessorDTO).toList();
    }

    public ProfessorDTO getProfessorById(Integer id) {
        ProfessorEntity professor = professorRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToProfessorDTO(professor);
    }

    public ProfessorDTO getProfessorByEmail(String email) {
        ProfessorEntity professor = professorRepository.findByEmail(email).orElseThrow(() -> notFoundByEmail(email));

        return convertToProfessorDTO(professor);
    }

    public ProfessorDTO getMe() {
        Integer currentId = getCurrentProfessorUserId();
        ProfessorEntity professor = professorRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return convertToProfessorDTO(professor);
    }

    public ProfessorDTO updateMe(UserUpdateRequest userUpdateRequest) {
        Integer currentId = getCurrentProfessorUserId();

        ProfessorEntity toUpdateProfessor = professorRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        if (professorRepository.findByAlias(userUpdateRequest.getAlias()).isPresent()) {
            throw new AliasAlreadyRegisteredException("User with alias " + userUpdateRequest.getAlias() + " already exists");
        }
        // Password validation
        if (!SecurityUtils.passwordValidation(userUpdateRequest.getPassword())) {
            throw new BadCredentialsException("Password is not valid");
        }

        toUpdateProfessor.setName(userUpdateRequest.getName());
        toUpdateProfessor.setSurname(userUpdateRequest.getSurname());
        toUpdateProfessor.setAlias(userUpdateRequest.getAlias());
        toUpdateProfessor.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        toUpdateProfessor.setProfilePhoto(userUpdateRequest.getProfilePhoto());

        ProfessorEntity updatedProfessor = professorRepository.save(toUpdateProfessor);
        return convertToProfessorDTO(updatedProfessor);
    }

    public void disableProfessor(Integer id) {
        ProfessorEntity toDeleteProfessor = professorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteProfessor.setEnabled(false);
        professorRepository.save(toDeleteProfessor);
    }

    public void enableProfessor(Integer id) {
        ProfessorEntity toDeleteProfessor = professorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteProfessor.setEnabled(true);
        professorRepository.save(toDeleteProfessor);
    }

    public void deleteMe() {
        Integer currentId = getCurrentProfessorUserId();
        ProfessorEntity toDeleteProfessor = professorRepository.findById(currentId)
                .orElseThrow(() -> notFoundById(currentId));

        // Logical delete
        toDeleteProfessor.setIsDeleted(true);
        toDeleteProfessor.setEnabled(false);
        professorRepository.save(toDeleteProfessor);
    }

    private ProfessorDTO convertToProfessorDTO(ProfessorEntity professor) {
        return new ProfessorDTO(professor);
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Professor with id %d not found", id));
    }

    private EntityNotFoundException notFoundByEmail(String email) {
        return new EntityNotFoundException(String.format("Professor with email %s not found", email));
    }
}
