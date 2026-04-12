package com.acasado.opored.service;

import com.acasado.opored.dto.PublicExaminationDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import com.acasado.opored.repository.CategoryRepository;
import com.acasado.opored.repository.PublicExaminationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PublicExaminationService {

    private final PublicExaminationRepository publicExaminationRepository;
    private final CategoryRepository categoryRepository;

    private final ForumService forumService;
    private final BulletinBoardService bulletinBoardService;

    public List<PublicExaminationDTO> getAllPublicExaminations() {
        return publicExaminationRepository.findAll().stream().map(this::convertToPublicExaminationDTO).toList();
    }

    public PublicExaminationDTO getPublicExaminationById(Integer id) {
        PublicExaminationEntity publicExamination = publicExaminationRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToPublicExaminationDTO(publicExamination);
    }

    public PublicExaminationDTO createPublicExamination(PublicExaminationDTO publicExaminationDTO) {
        if (!categoryRepository.existsById(publicExaminationDTO.getCategoryId())) {
            throw new EntityNotFoundException("Category with id " + publicExaminationDTO.getCategoryId() + " not found");
        }
        // Forum and BulletinBoard are forced to be created at the same time as the publicExamination.
        ForumEntity forum = new ForumEntity("Foro de " + publicExaminationDTO.getName(), publicExaminationDTO.getDescription());
        ForumEntity createdForum = forumService.createForum(forum);
        // We use the id of the forum to have the same id for all the entities
        BulletinBoardEntity bulletinBoard = new BulletinBoardEntity(createdForum.getId(), "Tablón de anuncios de " + publicExaminationDTO.getName(), publicExaminationDTO.getDescription());

        BulletinBoardEntity createdBulletinBoard = bulletinBoardService.createBulletinBoard(bulletinBoard);
        PublicExaminationEntity publicExamination = convertToPublicExamination(createdForum.getId(), publicExaminationDTO, createdForum, createdBulletinBoard);
        PublicExaminationEntity savedPublicExamination = publicExaminationRepository.save(publicExamination);
        return convertToPublicExaminationDTO(savedPublicExamination);
    }

    public PublicExaminationDTO updatePublicExamination(Integer id, String name, String description, Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category with id " + categoryId + " not found");
        }
        PublicExaminationEntity toUpdatePublicExamination = publicExaminationRepository.findById(id).orElseThrow(() -> notFoundById(id));

        toUpdatePublicExamination.setName(name);
        toUpdatePublicExamination.setDescription(description);
        toUpdatePublicExamination.setCategory(categoryRepository.getReferenceById(categoryId));

        PublicExaminationEntity updatedPublicExamination = publicExaminationRepository.save(toUpdatePublicExamination);
        return convertToPublicExaminationDTO(updatedPublicExamination);
    }

    public void deletePublicExamination(Integer id) {
        PublicExaminationEntity toDeletePublicExamination = publicExaminationRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeletePublicExamination.setDeleted(true);

        // Forum and BulletinBoard are forced to be deleted at the same time as the publicExamination.
        forumService.deleteForum(id);
        bulletinBoardService.deleteBulletinBoard(id);

        publicExaminationRepository.save(toDeletePublicExamination);
    }

    public Set<StudentSummaryDTO> getStudents(Integer id) {
        PublicExaminationEntity publicExamination = publicExaminationRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return publicExamination.getStudents().stream().map(StudentSummaryDTO::new).filter(StudentSummaryDTO::isEnabled).collect(Collectors.toSet());
    }

    private PublicExaminationDTO convertToPublicExaminationDTO(PublicExaminationEntity publicExamination) {
        return new PublicExaminationDTO(
                publicExamination.getId(),
                publicExamination.getName(),
                publicExamination.getDescription(),
                publicExamination.isVisible(),
                publicExamination.getCategory().getId(),
                publicExamination.getBulletinBoard().getId(),
                publicExamination.getForum().getId());
    }

    private PublicExaminationEntity convertToPublicExamination(Integer id, PublicExaminationDTO publicExaminationDTO, ForumEntity forum, BulletinBoardEntity bulletinBoard) {
        return new PublicExaminationEntity(
                id,
                publicExaminationDTO.getName(),
                publicExaminationDTO.getDescription(),
                categoryRepository.getReferenceById(publicExaminationDTO.getCategoryId()),
                bulletinBoard,
                forum);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Public examination with id %d not found", id));
    }
}
