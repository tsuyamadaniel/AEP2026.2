package com.aep.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Spring Boot.
 * Ao subir, a API fica disponível em http://localhost:8080
 * e o Swagger UI em http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
public class MonitorMedicamentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorMedicamentosApplication.class, args);
    }
}
