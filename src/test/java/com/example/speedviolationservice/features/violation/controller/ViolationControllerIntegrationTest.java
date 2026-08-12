package com.example.speedviolationservice.features.violation.controller;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

    @Test
    void shouldReturnBadRequestWhenLicensePlateIsInvalid() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1AA9",
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLicensePlateIsBlank() throws Exception {
        String requestBody = """
            {
                "licensePlate": "   ",
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLicensePlateIsMissing() throws Exception {
        String requestBody = """
        {
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenMeasuredSpeedIsZero() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 0,
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "2026-06-08T14:30:00Z"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenMeasuredSpeedIsNegative() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": -1,
            "speedLimit": 60,
            "equipmentId": "RAD-CWB-001",
            "captureTimestamp": "2026-06-08T14:30:00Z"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenMeasuredSpeedIsMissing() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "2026-06-08T14:30:00Z"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSpeedLimitIsZero() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": 92,
            "speedLimit": 0,
            "equipmentId": "RAD-CWB-001",
            "captureTimestamp": "2026-06-08T14:30:00Z"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSpeedLimitIsNegative() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": 92,
            "speedLimit": -1,
            "equipmentId": "RAD-CWB-001",
            "captureTimestamp": "2026-06-08T14:30:00Z"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSpeedLimitIsMissing() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": 92,
            "equipmentId": "RAD-CWB-001",
            "captureTimestamp": "2026-06-08T14:30:00Z"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEquipmentIdIsBlank() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 92,
                "speedLimit": 60,
                "equipmentId": "   ",
                "captureTimestamp": "2026-06-08T14:30:00Z"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEquipmentIdIsMissing() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": 92,
            "speedLimit": 60,
            "captureTimestamp": "2026-06-08T14:30:00Z"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCaptureTimestampIsInTheFuture() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 92,
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "2099-06-08T14:30:00Z"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCaptureTimestampFormatIsInvalid() throws Exception {
        String requestBody = """
            {
                "licensePlate": "ABC1D23",
                "measuredSpeed": 92,
                "speedLimit": 60,
                "equipmentId": "RAD-CWB-001",
                "captureTimestamp": "08/06/2026"
            }
            """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCaptureTimestampIsMissing() throws Exception {
        String requestBody = """
        {
            "licensePlate": "ABC1D23",
            "measuredSpeed": 92,
            "speedLimit": 60,
            "equipmentId": "RAD-CWB-001"
        }
        """;

        mockMvc.perform(post("/api/v1/violations/evaluate")
                        .header("x-origin", "FIXED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOriginHeaderIsMissing() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOriginHeaderIsInvalid() throws Exception {
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
                        .header("x-origin", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOriginHeaderIsLowercase() throws Exception {
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
                        .header("x-origin", "fixed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

}
