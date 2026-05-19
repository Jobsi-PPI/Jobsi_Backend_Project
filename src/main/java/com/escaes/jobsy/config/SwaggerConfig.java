package com.escaes.jobsy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Jobsi API")
                        .version("1.0")
                        .description("Plataforma web de micro-trabajos para los estudiantes del PCJIC. "
                                + "Este sistema permite la publicación, gestión y asignación de tareas con registro seguro "
                                + "y validación administrativa, optimizando el uso del tiempo libre, "
                                + "fortaleciendo la economía estudiantil y fomentando la ayuda mutua dentro de la comunidad universitaria.\n\n"
                                + "## Autenticación\n"
                                + "La API usa autenticación **JWT Bearer**. Para los endpoints protegidos, primero obtén un token "
                                + "en `POST /auth/login` y luego haz clic en **Authorize** e ingresa `<tu-token>`.")
                        .contact(new Contact()
                                .name("Equipo Jobsi")
                                .email("esteban.estra2004@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido desde POST /auth/login")));
    }
}
