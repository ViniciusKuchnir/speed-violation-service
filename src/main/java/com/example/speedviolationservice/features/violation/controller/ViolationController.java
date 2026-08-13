package com.example.speedviolationservice.features.violation.controller;

import com.example.speedviolationservice.features.violation.dto.EvaluateViolationRequest;
import com.example.speedviolationservice.features.violation.dto.EvaluateViolationResponse;
import com.example.speedviolationservice.features.violation.dto.FindViolationsRequest;
import com.example.speedviolationservice.features.violation.mapper.ViolationMapper;
import com.example.speedviolationservice.features.violation.model.SpeedReading;
import com.example.speedviolationservice.features.violation.model.ViolationOrigin;
import com.example.speedviolationservice.features.violation.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/violations")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateViolationResponse> evaluate(
            @RequestHeader("x-origin")ViolationOrigin origin,
            @Valid @RequestBody EvaluateViolationRequest request
    ) {
        var reading = new SpeedReading(
                request.licensePlate(),
                request.measuredSpeed(),
                request.speedLimit(),
                request.equipmentId(),
                request.captureTimestamp(),
                origin
        );

        var evaluation = violationService.evaluate(reading);

        return ResponseEntity.ok(
                ViolationMapper.toResponse(evaluation)
        );
    }

    @GetMapping
    public ResponseEntity<List<EvaluateViolationResponse>> findByLicensePlate(
            @Valid @ModelAttribute FindViolationsRequest request
    ) {
        var violations = violationService
                .findByLicensePlate(request.licensePlate())
                .stream()
                .map(ViolationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(violations);
    }

}
