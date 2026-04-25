package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.AnnouncementDTO;
import com.acasado.opored.service.AnnouncementService;
import com.acasado.opored.util.AnnouncementFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnouncementController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnnouncementControllerTest extends BaseControllerTest {

    @MockitoBean
    private AnnouncementService announcementService;

    @Test
    void When_GetUncategorizedAnnouncements_Expect_OkAndList() throws Exception {
        // Arrange
        List<AnnouncementDTO> dtoList = List.of(AnnouncementFactory.createValidAnnouncementDTO());
        when(announcementService.getUncategorizedAnnouncements()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/announcements/uncategorized")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetAnnouncementById_Expect_OkAndDTO() throws Exception {
        // Arrange
        AnnouncementDTO dto = AnnouncementFactory.createValidAnnouncementDTO();
        when(announcementService.getAnnouncementById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/announcements/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetAnnouncementById_DoesNotExist() throws Exception {
        // Arrange
        when(announcementService.getAnnouncementById(anyInt()))
                .thenThrow(new EntityNotFoundException("Announcement not found"));

        // Act
        mockMvc.perform(get("/api/announcements/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateAnnouncement_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        AnnouncementDTO inputDto = AnnouncementFactory.createValidAnnouncementDTO();
        when(announcementService.createAnnouncement(any(AnnouncementDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(inputDto.getTitle()));
    }

    @Test
    void Expect_BadRequest_When_CreateAnnouncement_InvalidData() throws Exception {
        // Arrange
        AnnouncementDTO invalidDto = AnnouncementFactory.createInvalidAnnouncementDTO();

        // Act
        mockMvc.perform(post("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(announcementService, never()).createAnnouncement(any());
    }

    @Test
    void When_UpdateAnnouncement_Expect_OkAndUpdatedDTO() throws Exception {
        // Arrange
        AnnouncementDTO updatedDto = AnnouncementFactory.createValidAnnouncementDTO();
        updatedDto.setTitle("Updated Title");

        when(announcementService.updateAnnouncement(eq(1), any(AnnouncementDTO.class)))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/announcements/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteAnnouncement_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(announcementService).deleteAnnouncement(anyInt());

        // Act
        mockMvc.perform(delete("/api/announcements/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(announcementService, times(1)).deleteAnnouncement(1);
    }
}