package com.acasado.opored.service;

import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.dto.TopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.ForumRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.repository.UserRepository;
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
public class TopicService {

    private final TopicRepository topicRepository;
    private final ForumRepository forumRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final MessageService messageService;

    public List<TopicDTO> getAllTopics() {
        return topicRepository.findAll().stream().map(this::convertToTopicDTO).toList();
    }

    public TopicDTO getTopicById(Integer id) {
        TopicEntity topic = topicRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToTopicDTO(topic);
    }

    public TopicDTO createTopic(TopicDTO topicDTO) {
        if (!forumRepository.existsById(topicDTO.getForumId())) {
            throw new EntityNotFoundException("Forum with id " + topicDTO.getForumId() + " not found");
        }
        UserEntity user = userRepository.findById(topicDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + topicDTO.getUserId() + " not found"));

        TopicEntity topic = convertToTopic(topicDTO);
        TopicEntity savedTopic = topicRepository.save(topic);

        // If the user who created the topic is a student, automatically follows it
        if (user instanceof StudentEntity) {
            studentService.followTopic(savedTopic.getId());
        }

        return convertToTopicDTO(savedTopic);
    }

    public TopicDTO updateMyTopic(Integer id, String title, String status) {
        TopicEntity toUpdateTopic = topicRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!toUpdateTopic.getUser().getId().equals(getCurrentUserId())) {
            throw new UserWithoutPermissionException("You do not have permissions to update this topic");
        }

        StatusEnum statusEnum = StatusEnum.valueOf(status);
        toUpdateTopic.setTitle(title);
        toUpdateTopic.setStatus(statusEnum);

        TopicEntity updatedTopic = topicRepository.save(toUpdateTopic);
        return convertToTopicDTO(updatedTopic);
    }

    public void deleteTopic(Integer id) {
        TopicEntity toDeleteTopic = topicRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteTopic.getUser().getId())) {
            throw new UserWithoutPermissionException("You do not have permissions to delete this message");
        }
        // Logical delete
        toDeleteTopic.setIsDeleted(true);
        topicRepository.save(toDeleteTopic);

        // Messages are forced to be deleted at the same time as their topic
        toDeleteTopic.getMessages().forEach(message -> messageService.deleteMessage(message.getId()));

        // Students are forced to unfollow a topic when it is deleted
        if (studentRepository.existsById(toDeleteTopic.getUser().getId())) {
            toDeleteTopic.getStudentsFollowing().forEach(student -> studentService.unfollowDeletedTopic(student.getId(), id));
        }
    }

    public void changeTopicsOwner(Set<TopicEntity> topics, UserEntity user) {
        Set<TopicEntity> changedOwnershipTopics = new HashSet<>();
        for (TopicEntity topicEntity : topics) {
            topicEntity.setUser(user);
            changedOwnershipTopics.add(topicEntity);
        }
        topicRepository.saveAll(changedOwnershipTopics);
    }

    public Set<StudentSummaryDTO> getStudentsFollowing(Integer id) {
        TopicEntity topic = topicRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return topic.getStudentsFollowing().stream().map(StudentSummaryDTO::new).filter(StudentSummaryDTO::isEnabled).collect(Collectors.toSet());
    }

    private TopicDTO convertToTopicDTO(TopicEntity topic) {
        return new TopicDTO(topic);
    }

    private TopicEntity convertToTopic(TopicDTO topicDTO) {
        return new TopicEntity(
                topicDTO.getTitle(),
                StatusEnum.valueOf(topicDTO.getStatus()),
                forumRepository.getReferenceById(topicDTO.getForumId()),
                userRepository.getReferenceById(topicDTO.getUserId()));
    }

    private Integer getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Topic with id %d not found", id));
    }
}
