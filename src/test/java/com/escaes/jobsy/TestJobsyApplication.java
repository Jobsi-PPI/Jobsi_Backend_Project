package com.escaes.jobsy;

import org.springframework.boot.SpringApplication;

/*
 * Arranque para desarrollo con PostgreSQL y Mailpit en contenedores (requiere Docker Desktop):
 *   mvnw.cmd spring-boot:test-run
 * Los datos se pierden al detener la aplicación.
 */
public class TestJobsyApplication {

    public static void main(String[] args) {
        SpringApplication.from(JobsyApplication::main)
                .with(TestcontainersConfiguration.class, DevServicesConfiguration.class)
                .run(args);
    }
}
