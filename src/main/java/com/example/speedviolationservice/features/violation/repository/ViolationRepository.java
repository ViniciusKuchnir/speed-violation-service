package com.example.speedviolationservice.features.violation.repository;

import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;

import java.util.List;

public interface ViolationRepository {
    void save(ViolationEvaluation evaluation);

    List<ViolationEvaluation> findByLicensePlate(String licensePlate);
}
