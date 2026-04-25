package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileUploadControllerTest extends BaseControllerTest {

    @MockitoBean
    private StorageService storageService;

    @Test
    void When_UploadFile_Expect_OkAndUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        when(storageService.store(any())).thenReturn("http://localhost:8080/uploads/uuid.pdf");

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("http://localhost:8080/uploads/uuid.pdf"));
    }

    @Test
    void When_UploadImage_Expect_OkAndUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        when(storageService.store(any())).thenReturn("http://localhost:8080/uploads/uuid.png");

        mockMvc.perform(multipart("/api/files/uploadImage").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("http://localhost:8080/uploads/uuid.png"));
    }
}
