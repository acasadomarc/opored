package com.acasado.opored.service;

import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.VideoEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.VideoRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;

    public List<VideoDTO> getAllVideos() {
        return videoRepository.findAll().stream().map(this::convertToVideoDTO).toList();
    }

    public VideoDTO getVideoById(Integer id) {
        VideoEntity video = videoRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToVideoDTO(video);
    }

    public VideoDTO createVideo(VideoDTO videoDTO) {
        Integer courseId = videoDTO.getCourseId();
        CourseEntity parentCourse = courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Parent course with id " + courseId + " not found"));
        if (!parentCourse.getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permission to add this videos to this course");
        }

        VideoEntity video = convertToVideoEntity(videoDTO);
        video.setCourse(parentCourse);
        VideoEntity savedVideo = videoRepository.save(video);
        return convertToVideoDTO(savedVideo);
    }

    public VideoDTO updateVideo(VideoDTO videoDTO) {
        VideoEntity toUpdateVideo = videoRepository.findById(videoDTO.getId()).orElseThrow(() -> notFoundById(videoDTO.getId()));

        if(!toUpdateVideo.getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to update this video");
        }

        toUpdateVideo.setTitle(videoDTO.getTitle());
        toUpdateVideo.setDescription(videoDTO.getDescription());
        toUpdateVideo.setDuration(videoDTO.getDuration());
        toUpdateVideo.setLink(videoDTO.getLink());

        VideoEntity updatedVideo = videoRepository.save(toUpdateVideo);
        return convertToVideoDTO(updatedVideo);
    }

    public void deleteVideo(Integer id) {
        VideoEntity toDeleteVideo = videoRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteVideo.getCourse().getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this video");
        }

        // Logical delete
        toDeleteVideo.setIsDeleted(true);
        videoRepository.save(toDeleteVideo);
    }

    private VideoDTO convertToVideoDTO(VideoEntity video) {
        return new VideoDTO(video);
    }

    private VideoEntity convertToVideoEntity(VideoDTO videoDTO) {
        return new VideoEntity(
                videoDTO.getTitle(),
                videoDTO.getDescription(),
                videoDTO.getDuration(),
                videoDTO.getLink());
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Video with id %d not found", id));
    }
}
