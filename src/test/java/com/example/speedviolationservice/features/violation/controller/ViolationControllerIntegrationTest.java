package com.example.speedviolationservice.features.violation.controller;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ViolationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldEvaluateViolationWhenSpeedExceedsLimit() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.licensePlate").value("ABC1D23"))
                .andExpect(jsonPath("$.equipmentId").value("RAD-CWB-001"))
                .andExpect(jsonPath("$.measuredSpeed").value(92))
                .andExpect(jsonPath("$.consideredSpeed").value(85))
                .andExpect(jsonPath("$.speedLimit").value(60))
                .andExpect(jsonPath("$.excessPercentage").value(41.67))
                .andExpect(jsonPath("$.hasViolation").value(true))
                .andExpect(jsonPath("$.violation.severity").value("SERIOUS"))
                .andExpect(jsonPath("$.violation.ctbCode").value("218-II"))
                .andExpect(jsonPath("$.processedAt").exists())
                .andExpect(jsonPath("$.processedAt").isNotEmpty());
    }

    @Test
    void shouldEvaluateWithoutViolationWhenConsideredSpeedIsBelowLimit() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 64,
                "speedLimit": 60,
                 "equipmentId": "RAD-CWB-001",
                 "captureTimestamp": "2026-06-08T14:30:00Z"
            }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.licensePlate").value("ABC1D23"))
                .andExpect(jsonPath("$.equipmentId").value("RAD-CWB-001"))
                .andExpect(jsonPath("$.measuredSpeed").value(64))
                .andExpect(jsonPath("$.consideredSpeed").value(57))
                .andExpect(jsonPath("$.speedLimit").value(60))
                .andExpect(jsonPath("$.excessPercentage").value(0))
                .andExpect(jsonPath("$.hasViolation").value(false))
                .andExpect(jsonPath("$.violation").value((Object) null))
                .andExpect(jsonPath("$.processedAt").exists())
                .andExpect(jsonPath("$.processedAt").isNotEmpty());
    }

}
