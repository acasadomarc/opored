package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.service.DocumentService;
import com.acasado.opored.util.DocumentFactory;
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

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest extends BaseControllerTest {

    @MockitoBean
    private DocumentService documentService;

    @Test
    void When_GetAllDocuments_Expect_OkAndList() throws Exception {
        // Arrange
        List<DocumentDTO> dtoList = List.of(DocumentFactory.createValidDocumentDTO());
        when(documentService.getAllDocuments()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Study Guide PDF"));
    }

    @Test
    void When_CreateDocument_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        DocumentDTO inputDto = DocumentFactory.createValidDocumentDTO();
        when(documentService.createDocument(any(DocumentDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(inputDto.getCourseId()));
    }

    @Test
    void Expect_BadRequest_When_CreateDocument_InvalidData() throws Exception {
        // Arrange
        DocumentDTO invalidDto = DocumentFactory.createInvalidDocumentDTO();

        // Act
        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(documentService, never()).createDocument(any());
    }

    @Test
    void When_UpdateMyDocument_Expect_OkAndDTO() throws Exception {
        // Arrange
        DocumentDTO inputDto = DocumentFactory.createValidDocumentDTO();
        inputDto.setTitle("Updated Title");
        when(documentService.updateDocument(any(DocumentDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(put("/api/documents/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteDocument_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(documentService).deleteDocument(anyInt());

        // Act
        mockMvc.perform(delete("/api/documents/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(documentService).deleteDocument(1);
    }
}