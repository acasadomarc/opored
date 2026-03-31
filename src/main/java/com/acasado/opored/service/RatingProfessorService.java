package com.acasado.opored.service;

import com.acasado.opored.dto.RatingProfessorDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.RatingProfessorEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.repository.RatingProfessorRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RatingProfessorService {

    private final RatingProfessorRepository ratingProfessorRepository;
    private final StudentRepository studentRepository;
    private  final ProfessorRepository professorRepository;

    public List<RatingProfessorDTO> getAllRatingProfessors() {
        return ratingProfessorRepository.findAll().stream().map(this::convertToRatingProfessorDTO).toList();
    }

    public RatingProfessorDTO getRatingProfessorById(Integer id) {
        RatingProfessorEntity rating = ratingProfessorRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToRatingProfessorDTO(rating);
    }

    public RatingProfessorDTO createRatingProfessor(RatingProfessorDTO ratingProfessorDTO) {
        if (!studentRepository.existsById(ratingProfessorDTO.getStudentId())) {
            throw new EntityNotFoundException("Student with id " + ratingProfessorDTO.getStudentId() + " not found");
        }
        if (!professorRepository.existsById(ratingProfessorDTO.getProfessorId())) {
            throw new EntityNotFoundException("Professor with id " + ratingProfessorDTO.getProfessorId() + " not found");
        }
        // Only one rating per student-professor
        boolean studentAlreadyPublishedRating = professorRepository
                .getReferenceById(ratingProfessorDTO.getProfessorId())
                .getRatings()
                .stream()
                .anyMatch(rating ->
                        rating.getStudent().getId().equals(ratingProfessorDTO.getStudentId())
                );

        if (studentAlreadyPublishedRating) {
            throw new StudentWithoutPermissionException("Student already published rating for this professor");
        }

        RatingProfessorEntity rating = convertToRatingProfessor(ratingProfessorDTO);
        RatingProfessorEntity savedRatingProfessor = ratingProfessorRepository.save(rating);

        return convertToRatingProfessorDTO(savedRatingProfessor);
    }

    public RatingProfessorDTO updateMyRatingProfessor(Integer id, String title, Float score, String comment) {
        RatingProfessorEntity toUpdateRatingProfessor = ratingProfessorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!toUpdateRatingProfessor.getStudent().getId().equals(getCurrentStudentUserId())) {
            throw new StudentWithoutPermissionException("You do not have permissions to update this rating");
        }

        toUpdateRatingProfessor.setTitle(title);
        toUpdateRatingProfessor.setScore(score);
        toUpdateRatingProfessor.setComment(comment);

        RatingProfessorEntity updatedRatingProfessor = ratingProfessorRepository.save(toUpdateRatingProfessor);
        return convertToRatingProfessorDTO(updatedRatingProfessor);
    }

    public void deleteRatingProfessor(Integer id) {
        RatingProfessorEntity toDeleteRatingProfessor = ratingProfessorRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteRatingProfessor.getStudent().getId())) {
            throw new StudentWithoutPermissionException("You do not have permissions to delete this rating");
        }

        // Logical delete
        toDeleteRatingProfessor.setIsDeleted(true);
        ratingProfessorRepository.save(toDeleteRatingProfessor);

    }

    public void deleteMultipleRatingProfessor(Set<RatingProfessorEntity> ratingProfessors) {
        for (RatingProfessorEntity ratingProfessorEntity : ratingProfessors) {
            ratingProfessorEntity.setIsDeleted(true);
            ratingProfessorRepository.save(ratingProfessorEntity);
        }
    }

    private RatingProfessorDTO convertToRatingProfessorDTO(RatingProfessorEntity rating) {
        return new RatingProfessorDTO(rating);
    }

    private RatingProfessorEntity convertToRatingProfessor(RatingProfessorDTO ratingProfessorDTO) {
        return new RatingProfessorEntity(
                ratingProfessorDTO.getTitle(),
                ratingProfessorDTO.getScore(),
                studentRepository.getReferenceById(ratingProfessorDTO.getStudentId()),
                professorRepository.getReferenceById(ratingProfessorDTO.getProfessorId()),
                ratingProfessorDTO.getComment());
    }

    private Integer getCurrentStudentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Rating with id %d not found", id));
    }
}
