package com.acasado.opored.service;

import com.acasado.opored.dto.AnswerDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.AnswerEntity;
import com.acasado.opored.model.QuestionEntity;
import com.acasado.opored.repository.AnswerRepository;
import com.acasado.opored.repository.QuestionRepository;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public List<AnswerDTO> getAllAnswers() {
        return answerRepository.findAll().stream().map(AnswerDTO::new).toList();
    }

    public AnswerDTO getAnswerById(Integer id) {
        AnswerEntity answer = answerRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToAnswerDTO(answer);
    }

    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        Integer questionId = answerDTO.getQuestionId();
        QuestionEntity parentQuestion = questionRepository.findById(questionId).orElseThrow(() -> new EntityNotFoundException("Parent question with id " + questionId + " not found"));
        if (!parentQuestion.getTest().getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permission to do add answers to this question");
        }

        AnswerEntity answerEntity = convertToAnswerEntity(answerDTO);
        answerEntity.setQuestion(parentQuestion);
        AnswerEntity savedAnswer = answerRepository.save(answerEntity);
        return convertToAnswerDTO(savedAnswer);
    }

    public AnswerDTO updateAnswer(AnswerDTO answerDTO) {
        AnswerEntity toUpdateAnswer = answerRepository.findById(answerDTO.getId()).orElseThrow(() -> notFoundById(answerDTO.getId()));

        if (!toUpdateAnswer.getQuestion().getTest().getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to update this answer");
        }

        toUpdateAnswer.setReply(answerDTO.getReply());
        toUpdateAnswer.setIsCorrect(answerDTO.getIsCorrect());

        AnswerEntity updatedAnswer = answerRepository.save(toUpdateAnswer);
        return convertToAnswerDTO(updatedAnswer);
    }

    public void deleteAnswer(Integer id) {
        AnswerEntity toDeleteAnswer = answerRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteAnswer.getQuestion().getTest().getCourse().getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this answer");
        }
        // Logical delete
        toDeleteAnswer.setIsDeleted(true);
        answerRepository.save(toDeleteAnswer);
    }

    private AnswerDTO convertToAnswerDTO(AnswerEntity answer) {
        return new AnswerDTO(answer);
    }

    private AnswerEntity convertToAnswerEntity(AnswerDTO answerDTO) {
        return new AnswerEntity(
                answerDTO.getReply(),
                answerDTO.getIsCorrect());
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Answer with id %d not found", id));
    }
}
