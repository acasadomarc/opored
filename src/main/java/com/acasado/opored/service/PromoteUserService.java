package com.acasado.opored.service;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.RoleAlreadyGrantedException;
import com.acasado.opored.model.ModeratorEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoteUserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ModeratorRepository moderatorRepository;
    private final AdministratorRepository administratorRepository;
    private final RoleRepository roleRepository;

    public void promoteToModerator(Integer id) {
        StudentEntity toPromoteStudent = studentRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (moderatorRepository.existsById(toPromoteStudent.getId())) {
            throw new RoleAlreadyGrantedException("User is already a moderator");
        }
        toPromoteStudent.setRole(roleRepository.getRoleByName(RoleEnum.MODERATOR));

        // Update role
        userRepository.save(toPromoteStudent);

        // Add to moderators table
        moderatorRepository.insertModerator(id);

    }

    public void demoteFromModerator(Integer id) {
        ModeratorEntity toDemoteModerator = moderatorRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!studentRepository.existsById(id)) {
            studentRepository.insertStudent(id);
        }
        toDemoteModerator.setRole(roleRepository.getRoleByName(RoleEnum.STUDENT));
        moderatorRepository.deleteModerator(id);

    }

    // Since the user can have both the STUDENT and MODERATOR roles, we send them the highest one they have.
    public void promoteToAdministrator(Integer id, String role) {
        RoleEnum roleEnum = RoleEnum.valueOf(role);
        UserEntity toPromoteUser;

        if (roleEnum == RoleEnum.STUDENT) {
            toPromoteUser = studentRepository.findById(id).orElseThrow(() -> notFoundById(id));
        }
        else {
            toPromoteUser = moderatorRepository.findById(id).orElseThrow(() -> notFoundById(id));
        }
        if (administratorRepository.existsById(toPromoteUser.getId())) {
            throw new RoleAlreadyGrantedException("User is already a administrator");
        }
        toPromoteUser.setRole(roleRepository.getRoleByName(RoleEnum.ADMIN));

        userRepository.save(toPromoteUser);

        administratorRepository.insertAdministrator(id);
    }

    public void demoteFromAdministrator(Integer id, String newRole) {
        RoleEnum roleEnum = RoleEnum.valueOf(newRole);
        UserEntity toDemoteUser = administratorRepository.findById(id).orElseThrow(() -> notFoundById(id));


        // Since the user can be demoted to student or moderator, the user may go to a class to which they did not previously belong, and must be updated accordingly.
        switch (roleEnum) {
            case STUDENT -> handleDemotionToStudent(id);
            case MODERATOR -> handleDemotionToModerator(id);
            default -> throw new EntityNotFoundException("Role " + roleEnum + " not found or not valid");
        }

        toDemoteUser.setRole(roleRepository.getRoleByName(roleEnum));


        userRepository.save(toDemoteUser);

        administratorRepository.deleteAdministrator(id);

    }

    private void handleDemotionToModerator(Integer id) {
        if (!moderatorRepository.existsById(id)) {
            moderatorRepository.insertModerator(id);
        }
    }

    private void handleDemotionToStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            studentRepository.insertStudent(id);
        }
        // Since he was a moderator, but the demotion is directly to the student role, he must also lose his status as moderator.
        if (moderatorRepository.existsById(id)) {
            moderatorRepository.deleteModerator(id);
        }
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("User with id %d not found", id));
    }
}
