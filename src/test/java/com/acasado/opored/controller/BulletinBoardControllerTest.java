package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.BulletinBoardDTO;
import com.acasado.opored.service.BulletinBoardService;
import com.acasado.opored.util.BulletinBoardFactory;
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

@WebMvcTest(BulletinBoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BulletinBoardControllerTest extends BaseControllerTest {

    @MockitoBean
    private BulletinBoardService bulletinBoardService;

    @Test
    void When_GetAllBulletinBoards_Expect_OkAndList() throws Exception {
        // Arrange
        List<BulletinBoardDTO> dtoList = List.of(BulletinBoardFactory.createValidBulletinBoardDTO());
        when(bulletinBoardService.getAllBulletinBoards()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/bulletinBoards")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("General Board"));
    }

    @Test
    void When_GetBulletinBoardById_Expect_OkAndDTO() throws Exception {
        // Arrange
        BulletinBoardDTO dto = BulletinBoardFactory.createValidBulletinBoardDTO();
        when(bulletinBoardService.getBulletinBoardById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/bulletinBoards/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetBulletinBoardById_DoesNotExist() throws Exception {
        // Arrange
        when(bulletinBoardService.getBulletinBoardById(anyInt()))
                .thenThrow(new EntityNotFoundException("Bulletin Board not found"));

        // Act
        mockMvc.perform(get("/api/bulletinBoards/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_UpdateBulletinBoard_Expect_OkAndUpdatedDTO() throws Exception {
        // Arrange
        BulletinBoardDTO updatedDto = BulletinBoardFactory.createValidBulletinBoardDTO();
        updatedDto.setName("Updated Name");

        when(bulletinBoardService.updateBulletinBoard(anyInt(), anyString(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/bulletinBoards/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }
}