package com.example.speedviolationservice.features.violation.controller;

import com.example.speedviolationservice.features.violation.dto.EvaluateViolationRequest;
import com.example.speedviolationservice.features.violation.dto.EvaluateViolationResponse;
import com.example.speedviolationservice.features.violation.dto.FindViolationsRequest;
import com.example.speedviolationservice.features.violation.exception.ApiError;
import com.example.speedviolationservice.features.violation.mapper.ViolationMapper;
import com.example.speedviolationservice.features.violation.model.SpeedReading;
import com.example.speedviolationservice.features.violation.model.ViolationOrigin;
import com.example.speedviolationservice.features.violation.service.ViolationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/violations")
@Tag(
        name = "Violations",
        description = "Speed violation evaluation and query operations"
)
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @PostMapping("/evaluate")
    @Operation(
            summary = "Evaluate a speed reading",
            description = """
                    Applies the configured tolerance to a vehicle speed reading,
                    calculates the excess percentage and determines whether the
                    reading represents a traffic violation.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Speed reading evaluated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = EvaluateViolationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or x-origin header",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ApiError.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ApiError.class
                            )
                    )
            )
    })
    public ResponseEntity<EvaluateViolationResponse> evaluate(
            @Parameter(
                    description = "Origin of the enforcement equipment",
                    required = true,
                    example = "FIXED",
                    schema = @Schema(
                            allowableValues = {
                                    "FIXED",
                                    "MOBILE",
                                    "HANDHELD"
                            }
                    )
            )
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
    @Operation(
            summary = "Find violations by license plate",
            description = """
                    Returns all speed violations currently stored in memory
                    for the informed vehicle license plate.
                    """,
            parameters = {
                    @Parameter(
                            name = "licensePlate",
                            description = "Vehicle license plate in old or Mercosul format",
                            required = true,
                            example = "ABC1D23",
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Violations retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = EvaluateViolationResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing license plate",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ApiError.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ApiError.class
                            )
                    )
            )
    })
    public ResponseEntity<List<EvaluateViolationResponse>> findByLicensePlate(
            @Parameter(hidden = true)
            @Valid
            @ModelAttribute
            FindViolationsRequest request
    ) {
        var violations = violationService
                .findByLicensePlate(request.licensePlate())
                .stream()
                .map(ViolationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(violations);
    }

}
