package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.service.PurchaseService;
import com.acasado.opored.util.PurchaseFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest extends BaseControllerTest {

    @MockitoBean
    private PurchaseService purchaseService;

    @Test
    void When_GetAllPurchases_Expect_OkAndList() throws Exception {
        // Arrange
        List<PurchaseDTO> dtoList = List.of(PurchaseFactory.createValidPurchaseDTO());
        when(purchaseService.getAllPurchases()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].paymentMethod").value("Credit Card"));
    }

    @Test
    void When_GetPurchaseById_Expect_OkAndDTO() throws Exception {
        // Arrange
        PurchaseDTO dto = PurchaseFactory.createValidPurchaseDTO();
        when(purchaseService.getPurchaseById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/purchases/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetPurchaseById_DoesNotExist() throws Exception {
        // Arrange
        when(purchaseService.getPurchaseById(anyInt()))
                .thenThrow(new EntityNotFoundException("Purchase not found"));

        // Act
        mockMvc.perform(get("/api/purchases/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreatePurchase_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        PurchaseDTO inputDto = PurchaseFactory.createValidPurchaseDTO();
        when(purchaseService.createPurchase(any(PurchaseDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(inputDto.getCourseId()));
    }

    @Test
    void Expect_BadRequest_When_CreatePurchase_InvalidData() throws Exception {
        // Arrange
        PurchaseDTO invalidDto = PurchaseFactory.createInvalidPurchaseDTO();

        // Act
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }
}