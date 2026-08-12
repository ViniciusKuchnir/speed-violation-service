package com.example.speedviolationservice.features.violation.provider;

import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;
import com.example.speedviolationservice.features.violation.repository.ViolationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class InMemoryViolationProvider implements ViolationRepository {

    private final Map<String, ConcurrentLinkedQueue<ViolationEvaluation>> storage =
            new ConcurrentHashMap<>();

    @Override
    public void save(ViolationEvaluation evaluation) {
        storage
                .computeIfAbsent(
                        evaluation.licensePlate(),
                        key -> new ConcurrentLinkedQueue<>()
                )
                .add(evaluation);
    }

    @Override
    public List<ViolationEvaluation> findByLicensePlate(String licensePlate) {
        var violations = storage.get(licensePlate);

        if (violations == null) {
            return List.of();
        }

        return List.copyOf(violations);
    }


}
