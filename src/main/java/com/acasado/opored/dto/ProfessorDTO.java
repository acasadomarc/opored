package com.acasado.opored.dto;

import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.RatingProfessorEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Professor data")
public class ProfessorDTO extends UserDTO {
    @Schema(description = "Set of ratings received by the professor")
    private Set<RatingProfessorDTO> ratings;

    @Schema(description = "Average score calculated from ratings", example = "4.5")
    private Float totalScore;

    public ProfessorDTO(ProfessorEntity professor) {
        setId(professor.getId());
        setName(professor.getName());
        setSurname(professor.getSurname());
        setAlias(professor.getAlias());
        setEmail(professor.getEmail());
        setPassword(professor.getPassword());
        setRegistrationDate(professor.getRegistrationDate());
        setProfilePhoto(professor.getProfilePhoto());
        setEnabled(professor.isEnabled());
        setAccountNoExpired(professor.isAccountNoExpired());
        setAccountNoLocked(professor.isAccountNoLocked());
        setCredentialNoExpired(professor.isCredentialNoExpired());
        setRatings(professor.getRatings());
        setTotalScore();
    }

    // Show only the ratings that are not marked as deleted
    public void setRatings(Set<RatingProfessorEntity> ratings) {
        Set<RatingProfessorEntity> publishedRatings = new LinkedHashSet<>();

        for (RatingProfessorEntity rating : ratings) {
            if (!rating.getIsDeleted()) {
                publishedRatings.add(rating);
            }
        }
        this.ratings = publishedRatings.stream().map(RatingProfessorDTO::new).collect(Collectors.toSet());
    }

    public void setTotalScore() {
        if (getRatings() != null && !getRatings().isEmpty()) {
            float sumScore = (float) getRatings().stream().mapToDouble(RatingProfessorDTO::getScore).sum();
            int totalVotes = getRatings().size();
            this.totalScore = sumScore / totalVotes;
        } else {
            this.totalScore = 0.0f;
        }
    }
}