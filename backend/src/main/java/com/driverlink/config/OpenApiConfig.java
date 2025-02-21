package com.driverlink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI driverLinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DriverLink API")
                        .description("Backend API for DriverLink - A community-driven road incident reporting system")
                        .version("1.0.0")
                        .license(new License().name("Private").url("https://driverlink.com")))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
