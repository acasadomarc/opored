package com.acasado.opored.service;

import com.acasado.opored.dto.ContentDTO;
import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.exception.RestrictedDeleteException;
import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ContentService contentService;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    public List<CourseDTO> getAllCourses(){
        return courseRepository.findAll().stream().map(this::convertToCourseDTO).toList();
    }

    public CourseDTO getCourseById(Integer id) {
        CourseEntity course = courseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!course.getIsVisible()) { // Block users trying to access the course by the id url
            if (!getCurrentUserId().equals(course.getProfessor().getId())) {
                throw new UserWithoutPermissionException("You are not authorized to view this course");
            }
        }
        return convertToCourseDTO(course);
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        CourseEntity course = convertToCourseEntity(courseDTO);
        CourseEntity savedCourse = courseRepository.save(course);
        return convertToCourseDTO(savedCourse);
    }

    public CourseDTO updateCourse(Integer id, String name, String description, Float price, Float discountPercentage, Boolean isVisible) {
        CourseEntity toUpdateCourse = courseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!toUpdateCourse.getProfessor().getId().equals(getCurrentUserId())) {
            throw notPermissionToUpdate();
        }

        toUpdateCourse.setName(name);
        toUpdateCourse.setDescription(description);
        toUpdateCourse.setPrice(price);
        toUpdateCourse.setDiscountPercentage(discountPercentage);
        if (isVisible != null) {
            toUpdateCourse.setIsVisible(isVisible);
        }
        CourseEntity updatedCourse = courseRepository.save(toUpdateCourse);
        return convertToCourseDTO(updatedCourse);
    }


    public CourseDTO addContent(Integer id, ContentDTO contentDTO) {
        CourseEntity course = courseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!course.getProfessor().getId().equals(getCurrentUserId())) {
            throw notPermissionToUpdate();
        }

        ContentEntity content = contentService.convertToEntity(contentDTO);

        course.getContents().add(content);
        CourseEntity updatedCourse = courseRepository.save(course);

        return convertToCourseDTO(updatedCourse);
    }

    public Float addDiscount(Integer id, Float percentage) {
        CourseEntity course = courseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!course.getProfessor().getId().equals(getCurrentUserId())) {
            throw notPermissionToUpdate();
        }
        course.setDiscountPercentage(percentage);
        courseRepository.save(course);

        return course.getPrice() * (1 - course.getDiscountPercentage());
    }

    public void deleteCourse(Integer id) {
        CourseEntity toDeleteCourse = courseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteCourse.getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this course");
        }

        if (!toDeleteCourse.getContents().isEmpty()) {
            throw new RestrictedDeleteException("You cannot delete a course with dependent contents. You must assign them a new course or delete them first.");
        }

        // Logical delete
        toDeleteCourse.setIsDeleted(true);
        courseRepository.save(toDeleteCourse);
    }

    public void changeCoursesOwner(Set<CourseEntity> courses, ProfessorEntity professor) {
        Set<CourseEntity> changedOwnershipCourses = new HashSet<>();
        for (CourseEntity courseEntity : courses) {
            courseEntity.setProfessor(professor);
            // Courses whose professor has deleted the account cannot be purchased anymore
            courseEntity.setIsPurchasable(false);
            changedOwnershipCourses.add(courseEntity);
        }
        courseRepository.saveAll(changedOwnershipCourses);
    }

    public Set<StudentSummaryDTO> getStudents(Integer courseId) {
        CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> notFoundById(courseId));
        return course.getPurchases().stream().map(PurchaseEntity::getStudent).map(StudentSummaryDTO::new).collect(Collectors.toSet());
    }

    private CourseDTO convertToCourseDTO(CourseEntity course) {
        return new CourseDTO(course);
    }

    private CourseEntity convertToCourseEntity(CourseDTO courseDTO) {
        Set<ContentDTO> contents = new HashSet<>();
        if (courseDTO.getContents() != null) {
            contents = courseDTO.getContents();
        }

        ProfessorEntity professor = professorRepository.findById(courseDTO.getProfessor().getId()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));

        return new CourseEntity(
                courseDTO.getName(),
                courseDTO.getDescription(),
                courseDTO.getPrice(),
                contents.stream().map(ContentDTO::toEntity).collect(Collectors.toSet()),
                setRatingCoursesEntities(courseDTO.getRatings()),
                professor
                );
    }

    private Set<RatingCourseEntity> setRatingCoursesEntities(Set<RatingCourseDTO> ratingCourseDTOS) {
        Set<RatingCourseEntity> ratingCourseEntities = new HashSet<>();
        if (ratingCourseDTOS != null) {
            for (RatingCourseDTO dto : ratingCourseDTOS) {
                ratingCourseEntities.add(new RatingCourseEntity(
                    dto.getTitle(),
                    dto.getScore(),
                    studentRepository.getReferenceById(dto.getStudentId()),
                    courseRepository.getReferenceById(dto.getCourseId()),
                    dto.getComment()));
            }
        }

        return ratingCourseEntities;
    }

    private Integer getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }


    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserAdmin() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Course with id %d not found", id));
    }

    private ProfessorWithoutPermissionException notPermissionToUpdate() {
        return new ProfessorWithoutPermissionException("You do not have permissions to update this course");
    }
}
