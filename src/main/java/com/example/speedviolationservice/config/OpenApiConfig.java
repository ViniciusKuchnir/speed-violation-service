package com.example.speedviolationservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Speed Violation Service API",
                version = "1.0.0",
                description = """
                        REST API for evaluating vehicle speed readings and
                        querying registered speed violations.
                        """
        ),
        tags = {
                @Tag(
                    name = "Violations",
                    description = "Speed violation evaluation and query operations"
                )
        }
)
public class OpenApiConfig {
}
