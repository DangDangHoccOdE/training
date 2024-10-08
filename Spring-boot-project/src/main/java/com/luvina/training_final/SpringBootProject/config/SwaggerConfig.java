package com.luvina.training_final.SpringBootProject.config;

//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.info.License;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//
//import java.util.List;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI openApi() {
//        return new OpenAPI()
//                .info(new Info().title("Dangcode").version("version").description("description")
//                        .license(new License().name("Api license").url("http://domain.vn/license")))
//                .servers(List.of(new Server().url("http://localhost:8080").description("serverName")))
//                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
//                        .type(SecurityScheme.Type.HTTP)
//                        .scheme("bearer")
//                        .bearerFormat("JWT")))
//                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
//    }
//
//    @Bean
//    public GroupedOpenApi groupedOpenApi() {
//        return GroupedOpenApi.builder()
//                .group("api-service-v1")
//                .packagesToScan("com.luvina.training_final.SpringBootProject.controller")
//                .build();
//    }
//}

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
//    @Value("${base-url}")
//    private String url;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Project API")
                        .version("1.0")
                        .description("API documentation for the Spring Boot project"));
    }

    // Cấu hình để Swagger quét các API của bạn
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**") // Tùy chỉnh đường dẫn cho các API của bạn
                .build();
    }
}

