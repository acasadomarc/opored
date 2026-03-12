package com.acasado.opored.util;

import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.VideoEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class VideoFactory {

    public static VideoDTO createValidVideoDTO() {
        return new VideoDTO(
                1,
                "Intro to Spring",
                "Basic concepts",
                120,
                "https://video.link",
                10 // Course ID
        );
    }

    public static VideoDTO createInvalidVideoDTO() {
        return new VideoDTO(
                null,
                null,
                "Desc",
                -10,
                null,
                null
        );
    }

    public static VideoEntity createValidVideoEntity() {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setProfessor(professor);

        VideoEntity video = new VideoEntity();
        video.setId(1);
        video.setTitle("Intro to Spring");
        video.setDescription("Basic concepts");
        video.setDuration(120);
        video.setLink("https://video.link");
        video.setIsDeleted(false);
        video.setCourse(course);

        return video;
    }
}