package com.escaes.jobsy;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.logging.Logger;

/*
 * Servicios adicionales solo para desarrollo: Mailpit captura los correos enviados por la app
 * (no salen a internet) y los muestra en su interfaz web.
 */
@TestConfiguration(proxyBeanMethods = false)
public class DevServicesConfiguration {

    private static final int PUERTO_SMTP = 1025;

    private static final int PUERTO_WEB = 8025;

    private static final Logger logger = Logger.getLogger(DevServicesConfiguration.class.getName());

    @Bean
    GenericContainer<?> mailpitContainer() {
        return new GenericContainer<>(DockerImageName.parse("axllent/mailpit:v1.27"))
                .withExposedPorts(PUERTO_SMTP, PUERTO_WEB);
    }

    @Bean
    DynamicPropertyRegistrar mailpitProperties(GenericContainer<?> mailpitContainer) {
        return registry -> {
            registry.add("spring.mail.host", mailpitContainer::getHost);
            registry.add("spring.mail.port", () -> mailpitContainer.getMappedPort(PUERTO_SMTP));
        };
    }

    @Bean
    ApplicationRunner mailpitUrlLogger(GenericContainer<?> mailpitContainer) {
        return args -> logger.info("Bandeja de Mailpit: http://" + mailpitContainer.getHost() + ":"
                + mailpitContainer.getMappedPort(PUERTO_WEB));
    }
}
