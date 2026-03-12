package com.acasado.opored.service;

import com.acasado.opored.dto.*;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsService userDetailsService;

    private final TopicRepository topicRepository;
    private final FollowTopicRepository followTopicRepository;
    private final PublicExaminationRepository publicExaminationRepository;
    private final StudentPublicExaminationRepository studentPublicExaminationRepository;

    public List<StudentSummaryDTO> getAllStudents() {
        return studentRepository.findAll().stream().map(StudentSummaryDTO::new).toList();
    }

    public StudentSummaryDTO getStudentById(Integer id) {
        StudentEntity student = studentRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return new StudentSummaryDTO(student);
    }

    public StudentSummaryDTO getStudentByEmail(String email) {
        StudentEntity student = studentRepository.findByEmail(email).orElseThrow(() -> notFoundByEmail(email));

        return new StudentSummaryDTO(student);
    }

    public StudentDTO getMe() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return convertToStudentDTO(student);
    }

    public AuthResponse signUp(AuthCreateUserRequest authCreateUserRequest) {
        return userDetailsService.createUser(authCreateUserRequest);
    }

    public StudentDTO updateMe(UserUpdateRequest userUpdateRequest) {
        Integer currentId = getCurrentStudentUserId();

        StudentEntity toUpdateStudent = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        if (studentRepository.findByAlias(userUpdateRequest.getAlias()).isPresent()) {
            throw new AliasAlreadyRegisteredException("User with alias " + userUpdateRequest.getAlias() + " already exists");
        }

        toUpdateStudent.setName(userUpdateRequest.getName());
        toUpdateStudent.setSurname(userUpdateRequest.getSurname());
        toUpdateStudent.setAlias(userUpdateRequest.getAlias());
        if (!userUpdateRequest.getPassword().isEmpty()) {
            if (!SecurityUtils.passwordValidation(userUpdateRequest.getPassword())) {
                throw new BadCredentialsException("Password is not valid");
            }
            toUpdateStudent.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        toUpdateStudent.setProfilePhoto(userUpdateRequest.getProfilePhoto());

        StudentEntity updatedStudent = studentRepository.save(toUpdateStudent);
        return convertToStudentDTO(updatedStudent);
    }

    public void deleteStudent(Integer id) {
        StudentEntity toDeleteStudent = studentRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteStudent.setIsDeleted(true);
        toDeleteStudent.setEnabled(false);
        studentRepository.save(toDeleteStudent);
    }

    public void deleteMe() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity toDeleteStudent = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));

        // Logical delete
        toDeleteStudent.setIsDeleted(true);
        toDeleteStudent.setEnabled(false);
        studentRepository.save(toDeleteStudent);
    }

    public Set<TopicSummaryDTO> getFollowedTopics() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return student.getTopicsFollowed().stream().map(TopicSummaryDTO::new).collect(Collectors.toSet());
    }

    // Student - Topic relation methods
    public void followTopic(Integer topicId) {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        TopicEntity topic = topicRepository.findById(topicId).orElseThrow(() -> new EntityNotFoundException("Topic with id " + topicId + " not found"));

        FollowTopicId followTopicId = new FollowTopicId(topicId, student.getId());
        if (followTopicRepository.existsById(followTopicId)) {
            throw new IllegalStateException("Student has already followed the topic");
        }
        followTopicRepository.save(new FollowTopic(followTopicId, topic, student));
    }

    public void unfollowTopic(Integer topicId) {
        FollowTopicId followTopicId = new FollowTopicId(topicId, getCurrentStudentUserId());
        if (!followTopicRepository.existsById(followTopicId)) {
            throw new EntityNotFoundException("Relation between student and topic with id " + followTopicId + " not found");
        }
        followTopicRepository.deleteById(followTopicId);
    }

    // Used when the topics hav been deleted, and it is needed to make all the students unfollow at the same time
    public void unfollowDeletedTopic(Integer studentId, Integer topicId) {
        FollowTopicId followTopicId = new FollowTopicId(topicId, studentId);
        if (!followTopicRepository.existsById(followTopicId)) {
            throw new EntityNotFoundException("Relation between student and topic with id " + followTopicId + " not found");
        }
        followTopicRepository.deleteById(followTopicId);
    }

    // Student - Public Examination relation methods

    public Set<PublicExaminationSummaryDTO> getEnrolledPublicExaminations() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return student.getPublicExaminations().stream().map(PublicExaminationSummaryDTO::new).collect(Collectors.toSet());
    }

    public void signUpForPublicExamination(Integer publicExaminationId) {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        PublicExaminationEntity publicExamination = publicExaminationRepository.findById(publicExaminationId).orElseThrow(() -> new EntityNotFoundException("Public examination with id " + publicExaminationId + " not found"));

        StudentPublicExaminationId studentPublicExaminationId = new StudentPublicExaminationId(student.getId(), publicExaminationId);
        studentPublicExaminationRepository.save(new StudentPublicExamination(studentPublicExaminationId, student, publicExamination));
    }

    public void withdrawFromPublicExamination(Integer publicExaminationId) {
        StudentPublicExaminationId studentPublicExaminationId = new StudentPublicExaminationId(getCurrentStudentUserId(), publicExaminationId);
        if (!studentPublicExaminationRepository.existsById(studentPublicExaminationId)) {
            throw new EntityNotFoundException("Relation between student and publicExamination with id " + studentPublicExaminationId + " not found");
        }
        studentPublicExaminationRepository.deleteById(studentPublicExaminationId);
    }

    // Student - Course/Purchase relation methods

    public Set<CourseDTO> getCourses() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return student.getPurchases().stream().map(PurchaseEntity::getCourse).map(CourseDTO::new).collect(Collectors.toSet());
    }

    public Set<PurchaseDTO> getPurchases() {
        Integer currentId = getCurrentStudentUserId();
        StudentEntity student = studentRepository.findById(currentId).orElseThrow(() -> notFoundById(currentId));
        return student.getPurchases().stream().map(PurchaseDTO::new).collect(Collectors.toSet());
    }

    private StudentDTO convertToStudentDTO(StudentEntity student) {
        return new StudentDTO(student);
    }

    private Integer getCurrentStudentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Student with id %d not found", id));
    }

    private EntityNotFoundException notFoundByEmail(String email) {
        return new EntityNotFoundException(String.format("Student with email %s not found", email));
    }
}
