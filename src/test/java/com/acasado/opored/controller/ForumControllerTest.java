package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.ForumDTO;
import com.acasado.opored.service.ForumService;
import com.acasado.opored.util.ForumFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumControllerTest extends BaseControllerTest {

    @MockitoBean
    private ForumService forumService;

    @Test
    void When_GetAllForums_Expect_OkAndList() throws Exception {
        // Arrange
        List<ForumDTO> dtoList = List.of(ForumFactory.createValidForumDTO());
        when(forumService.getAllForums()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/forums")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Public Exams Forum"));
    }

    @Test
    void When_GetForumById_Expect_OkAndDTO() throws Exception {
        // Arrange
        ForumDTO dto = ForumFactory.createValidForumDTO();
        when(forumService.getForumById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/forums/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetForumById_DoesNotExist() throws Exception {
        // Arrange
        when(forumService.getForumById(anyInt()))
                .thenThrow(new EntityNotFoundException("Forum not found"));

        // Act
        mockMvc.perform(get("/api/forums/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_UpdateForum_Expect_OkAndUpdatedDTO() throws Exception {
        // Arrange
        ForumDTO updatedDto = ForumFactory.createValidForumDTO();
        updatedDto.setName("Updated Forum Name");

        when(forumService.updateForum(anyInt(), anyString(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/forums/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Forum Name"));
    }
}