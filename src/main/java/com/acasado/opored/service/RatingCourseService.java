package com.acasado.opored.service;

import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.RatingCourseEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.RatingCourseRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingCourseService {

    private final RatingCourseRepository ratingCourseRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<RatingCourseDTO> getAllRatingCourses() {
        return ratingCourseRepository.findAll().stream().map(this::convertToRatingCourseDTO).toList();
    }

    public RatingCourseDTO getRatingCourseById(Integer id) {
        RatingCourseEntity rating = ratingCourseRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToRatingCourseDTO(rating);
    }

    public RatingCourseDTO createRatingCourse(RatingCourseDTO ratingCourseDTO) {
        if (!studentRepository.existsById(ratingCourseDTO.getStudentId())) {
            throw new EntityNotFoundException("Student with id " + ratingCourseDTO.getStudentId() + " not found");
        }
        if (!courseRepository.existsById(ratingCourseDTO.getCourseId())) {
            throw new EntityNotFoundException("Course with id " + ratingCourseDTO.getCourseId() + " not found");
        }
        // Only one rating per student-course
        boolean studentAlreadyPublishedRating = courseRepository
                .getReferenceById(ratingCourseDTO.getCourseId())
                .getRatings()
                .stream()
                .anyMatch(rating ->
                        rating.getStudent().getId().equals(ratingCourseDTO.getStudentId())
                );

        if (studentAlreadyPublishedRating) {
            // Check if the student had previously published a rating and it is currently deleted
            Optional<RatingCourseEntity> optionalRating =
                    ratingCourseRepository.findByStudentId(ratingCourseDTO.getStudentId());

            if (optionalRating.isPresent()) {
                RatingCourseEntity ratingCourseEntity = optionalRating.get();

                return updateMyRatingCourse(
                        ratingCourseEntity.getId(),
                        ratingCourseDTO.getTitle(),
                        ratingCourseDTO.getScore(),
                        ratingCourseDTO.getComment()
                );
            }
            else {
                throw new StudentWithoutPermissionException("Student already published rating for this course");
            }
        }
        RatingCourseEntity rating = convertToRatingCourse(ratingCourseDTO);
        RatingCourseEntity savedRatingCourse = ratingCourseRepository.save(rating);

        return convertToRatingCourseDTO(savedRatingCourse);
    }

    public RatingCourseDTO updateMyRatingCourse(Integer id, String title, Float score, String comment) {
        RatingCourseEntity toUpdateRatingCourse = ratingCourseRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!toUpdateRatingCourse.getStudent().getId().equals(getCurrentStudentUserId())) {
            throw new StudentWithoutPermissionException("You do not have permissions to update this rating");
        }
        toUpdateRatingCourse.setTitle(title);
        toUpdateRatingCourse.setScore(score);
        toUpdateRatingCourse.setComment(comment);
        toUpdateRatingCourse.setDeleted(false);

        RatingCourseEntity updatedRatingCourse = ratingCourseRepository.save(toUpdateRatingCourse);
        return convertToRatingCourseDTO(updatedRatingCourse);
    }

    public void deleteRatingCourse(Integer id) {
        RatingCourseEntity toDeleteRatingCourse = ratingCourseRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteRatingCourse.getStudent().getId())) {
            throw new StudentWithoutPermissionException("You do not have permissions to delete this rating");
        }
        // Logical delete
        toDeleteRatingCourse.setDeleted(true);
        ratingCourseRepository.save(toDeleteRatingCourse);
    }

    private RatingCourseDTO convertToRatingCourseDTO(RatingCourseEntity rating) {
        return new RatingCourseDTO(rating);
    }

    private RatingCourseEntity convertToRatingCourse(RatingCourseDTO ratingCourseDTO) {
        return new RatingCourseEntity(
                ratingCourseDTO.getTitle(),
                ratingCourseDTO.getScore(),
                studentRepository.getReferenceById(ratingCourseDTO.getStudentId()),
                courseRepository.getReferenceById(ratingCourseDTO.getCourseId()),
                ratingCourseDTO.getComment());
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