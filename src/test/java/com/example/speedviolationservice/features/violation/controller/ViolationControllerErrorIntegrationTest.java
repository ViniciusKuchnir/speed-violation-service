package com.example.speedviolationservice.features.violation.controller;

import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;
import com.example.speedviolationservice.features.violation.repository.ViolationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ViolationControllerErrorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ViolationRepository violationRepository;

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs()
            throws Exception {

        doThrow(new RuntimeException("simulated internal failure"))
                .when(violationRepository)
                .save(any(ViolationEvaluation.class));

        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 92,
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "2026-06-08T14:30:00Z"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(content().string(
                        not(containsString("simulated internal failure"))
                ));
    }
}
