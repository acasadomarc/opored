package com.acasado.opored.service;

import com.acasado.opored.dto.ForumDTO;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.repository.ForumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumRepository forumRepository;
    private final TopicService topicService;

    public List<ForumDTO> getAllForums() {
        return forumRepository.findAll().stream().map(this::convertToForumDTO).toList();
    }

    public ForumDTO getForumById(Integer id) {
        ForumEntity forum = forumRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToForumDTO(forum);
    }
    public ForumEntity createForum(ForumEntity forum) {
        return forumRepository.save(forum);
    }

    public ForumDTO updateForum(Integer id, String name, String description) {
        ForumEntity toUpdateForum = forumRepository.findById(id).orElseThrow(() -> notFoundById(id));

        toUpdateForum.setName(name);
        toUpdateForum.setDescription(description);

        ForumEntity updatedForum = forumRepository.save(toUpdateForum);
        return convertToForumDTO(updatedForum);
    }
    public void deleteForum(Integer id) {
        ForumEntity toDeleteForum = forumRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        toDeleteForum.setIsDeleted(true);
        forumRepository.save(toDeleteForum);
        // Topics are forced to be deleted at the same time as their forum
        toDeleteForum.getTopics().forEach(topicEntity -> topicService.deleteTopic(topicEntity.getId()));

    }

    private ForumDTO convertToForumDTO(ForumEntity forum) {
        return new ForumDTO(forum);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Forum with id %d not found", id));
    }
}
