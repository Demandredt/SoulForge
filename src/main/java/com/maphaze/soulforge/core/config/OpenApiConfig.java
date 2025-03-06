package com.maphaze.soulforge.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SoulForge 文档",
                version = "1.0"
        )
)
public class OpenApiConfig {
}
