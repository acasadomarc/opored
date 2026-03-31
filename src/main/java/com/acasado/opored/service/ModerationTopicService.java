package com.acasado.opored.service;

import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.repository.ModerationTopicRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModerationTopicService {

    private final ModeratorRepository moderatorRepository;
    private final TopicRepository topicRepository;
    private final ModerationTopicRepository moderationTopicRepository;
    private final StudentService studentService;
    private final MessageService messageService;

    public List<ModerationTopicDTO> getAllModeratedTopics() {
        return moderationTopicRepository.findAll().stream().map(this::convertToModerationTopicDTO).toList();
    }

    public List<ModerationTopicDTO> getMyModeratedTopics() {
        return moderationTopicRepository.findAll().stream().filter(moderationTopicEntity -> Objects.equals(moderationTopicEntity.getModerator().getId(), getCurrentModeratorUserId())).map(this::convertToModerationTopicDTO).toList();
    }

    public ModerationTopicDTO getModerationTopicById(Integer topicId, Integer moderatorId) {
        ModerationTopicId moderationTopicId = new ModerationTopicId(topicId, moderatorId);
        ModerationTopicEntity moderatedTopic = moderationTopicRepository.findById(moderationTopicId).orElseThrow(() -> notFoundById(topicId));

        return convertToModerationTopicDTO(moderatedTopic);
    }


    public ModerationTopicDTO moderateTopic(ModerationTopicDTO moderationTopicDTO) {
        TopicEntity topic = topicRepository.findById(moderationTopicDTO.getTopicId()).orElseThrow(() -> new EntityNotFoundException("Topic with id " + moderationTopicDTO.getTopicId() + " not found"));
        moderatorRepository.findById(moderationTopicDTO.getModeratorId()).orElseThrow(() -> new EntityNotFoundException("Moderator with id " + moderationTopicDTO.getModeratorId() + " not found"));

        ModerationTopicEntity moderatedTopic = convertToModerationTopic(moderationTopicDTO);
        moderationTopicRepository.save(moderatedTopic);

        topic.setStatus(StatusEnum.HIDDEN);
        topicRepository.save(topic);

        // Students are forced to unfollow a topic when it is deleted
        topic.getStudentsFollowing().forEach(student -> studentService.unfollowDeletedTopic(student.getId(), topic.getId()));

        // Messages of the topic are hidden as well
        topic.getMessages().forEach(message -> messageService.hideMessageInModeratedTopic(message.getId()));

        return convertToModerationTopicDTO(moderatedTopic);
    }

    public ModerationTopicDTO updateModeratedTopicByMe(Integer topicId, String reason) {
        Integer currentId = getCurrentModeratorUserId();
        ModerationTopicId moderationTopicId = new ModerationTopicId(topicId, currentId);
        ModerationTopicEntity toUpdateModerationTopic = moderationTopicRepository.findById(moderationTopicId)
                .orElseThrow(() -> notFoundById(topicId));

        toUpdateModerationTopic.setReason(reason);

        ModerationTopicEntity updatedModerationTopic = moderationTopicRepository.save(toUpdateModerationTopic);
        return convertToModerationTopicDTO(updatedModerationTopic);
    }

    public void deleteModerationTopic(Integer topicId, Integer moderatorId) {
        ModerationTopicId moderationTopicId = new ModerationTopicId(topicId, moderatorId);
        ModerationTopicEntity toDeleteModerationTopic = moderationTopicRepository.findById(moderationTopicId)
                .orElseThrow(() -> notFoundById(topicId));

        // Hard delete to allow to moderate the same topic by the same moderator again
        moderationTopicRepository.delete(toDeleteModerationTopic);

        TopicEntity topic = topicRepository.getReferenceById(topicId);
        topic.setStatus(StatusEnum.VISIBLE);
        topicRepository.save(topic);

        // Recover the messages
        topic.getMessages().forEach(message -> message.setStatus(StatusEnum.VISIBLE));
    }

    public void changeModerationTopicsOwner(Set<ModerationTopicEntity> moderationTopics, ModeratorEntity moderator) {
        Set<ModerationTopicEntity> changedOwnershipModerationTopics = new HashSet<>();
        for (ModerationTopicEntity moderationmoderationTopicEntity : moderationTopics) {
            moderationmoderationTopicEntity.setModerator(moderator);
            changedOwnershipModerationTopics.add(moderationmoderationTopicEntity);
        }
        moderationTopicRepository.saveAll(changedOwnershipModerationTopics);
    }

    private ModerationTopicDTO convertToModerationTopicDTO(ModerationTopicEntity moderationTopicEntity) {
        return new ModerationTopicDTO(
                moderationTopicEntity.getId().getTopicId(),
                moderationTopicEntity.getId().getModeratorId(),
                moderationTopicEntity.getModerationDate(),
                moderationTopicEntity.getReason());
    }

    private ModerationTopicEntity convertToModerationTopic(ModerationTopicDTO moderationTopicDTO) {
        return new ModerationTopicEntity(
                new ModerationTopicId(moderationTopicDTO.getTopicId(), moderationTopicDTO.getModeratorId()),
                topicRepository.getReferenceById(moderationTopicDTO.getTopicId()),
                moderatorRepository.getReferenceById(moderationTopicDTO.getModeratorId()),
                moderationTopicDTO.getReason());
    }

    private Integer getCurrentModeratorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Moderated topic with id %d not found", id));
    }
}
