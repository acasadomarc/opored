package com.acasado.opored.service;

import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final CourseService courseService;
    private final RatingProfessorService ratingProfessorService;
    private final ProfessorRepository professorRepository;

    private final static Integer DEFAULT_DELETED_PROFESSOR_ID = 2;

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

    public Set<CourseDTO> getCourses() {
        Integer currentId = getCurrentProfessorUserId();
        ProfessorEntity professor = professorRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return professor.getCourses().stream().map(CourseDTO::new).collect(Collectors.toSet());
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

    public void deleteMe(ProfessorEntity toDeleteProfessor) {

        // User to reference in the existent topics and messages
        ProfessorEntity defaultDeletedProfessor = professorRepository.findById(14).orElseThrow(() -> notFoundById(DEFAULT_DELETED_PROFESSOR_ID));

        if (!toDeleteProfessor.getCourses().isEmpty()) {
            courseService.changeCoursesOwner(toDeleteProfessor.getCourses(), defaultDeletedProfessor);
        }
        // Ratings are deleted
        if (!toDeleteProfessor.getRatings().isEmpty()) {
            ratingProfessorService.deleteMultipleRatingProfessor(toDeleteProfessor.getRatings());
        }

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
