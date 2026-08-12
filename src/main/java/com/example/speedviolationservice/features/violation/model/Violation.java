package com.example.speedviolationservice.features.violation.model;

public record Violation(
        ViolationSeverity severity
) {
    public String ctbCode() {
        return severity.getCtbCode();
    }
}
