package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.service.VideoService;
import com.acasado.opored.util.VideoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc(addFilters = false)
class VideoControllerTest extends BaseControllerTest {

    @MockitoBean
    private VideoService videoService;

    @Test
    void When_GetAllVideos_Expect_OkAndList() throws Exception {
        // Arrange
        List<VideoDTO> dtoList = List.of(VideoFactory.createValidVideoDTO());
        when(videoService.getAllVideos()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Intro to Spring"));
    }

    @Test
    void When_CreateVideo_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        VideoDTO inputDto = VideoFactory.createValidVideoDTO();
        when(videoService.createVideo(any(VideoDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(inputDto.getCourseId()));
    }

    @Test
    void Expect_BadRequest_When_CreateVideo_InvalidData() throws Exception {
        // Arrange
        VideoDTO invalidDto = VideoFactory.createInvalidVideoDTO();

        // Act
        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(videoService, never()).createVideo(any());
    }

    @Test
    void When_UpdateMyVideo_Expect_OkAndDTO() throws Exception {
        // Arrange
        VideoDTO inputDto = VideoFactory.createValidVideoDTO();
        inputDto.setTitle("Updated Title");
        when(videoService.updateVideo(any(VideoDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(put("/api/videos/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteVideo_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(videoService).deleteVideo(anyInt());

        // Act
        mockMvc.perform(delete("/api/videos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(videoService).deleteVideo(1);
    }
}