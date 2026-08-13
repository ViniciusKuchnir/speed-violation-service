package com.example.speedviolationservice.features.violation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ViolationQueryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnStoredViolationsWhenLicensePlateExists() throws Exception {
        evaluate("QWE1R23", 92);

        mockMvc.perform(get("/api/v1/violations")
                        .param("licensePlate", "QWE1R23"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].licensePlate").value("QWE1R23"))
                .andExpect(jsonPath("$[0].equipmentId").value("RAD-CWB-001"))
                .andExpect(jsonPath("$[0].measuredSpeed").value(92))
                .andExpect(jsonPath("$[0].consideredSpeed").value(85))
                .andExpect(jsonPath("$[0].speedLimit").value(60))
                .andExpect(jsonPath("$[0].excessPercentage").value(41.67))
                .andExpect(jsonPath("$[0].hasViolation").value(true))
                .andExpect(jsonPath("$[0].violation.severity").value("SERIOUS"))
                .andExpect(jsonPath("$[0].violation.ctbCode").value("218-II"))
                .andExpect(jsonPath("$[0].processedAt").exists());
    }

    @Test
    void shouldReturnEmptyListWhenLicensePlateHasNoViolations() throws Exception {
        mockMvc.perform(get("/api/v1/violations")
                        .param("licensePlate", "ZZZ9Z99"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldNotReturnEvaluationWithoutViolation() throws Exception {
        evaluate("NOP1Q23", 64);

        mockMvc.perform(get("/api/v1/violations")
                        .param("licensePlate", "NOP1Q23"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenQueryLicensePlateIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/violations")
                        .param("licensePlate", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("INVALID_LICENSE_PLATE"))
                .andExpect(jsonPath("$.message").value("Invalid license plate format"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenQueryLicensePlateIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/violations"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("INVALID_LICENSE_PLATE"))
                .andExpect(jsonPath("$.message").value("License plate is required"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    private void evaluate(String licensePlate, int measuredSpeed) throws Exception {
        String requestBody = """
            {
                "licensePlate": "%s",
                "measuredSpeed": %d,
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "2026-06-08T14:30:00Z"
            }
            """.formatted(licensePlate, measuredSpeed);

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}
