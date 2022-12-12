package org.rrajesh1979.urlshort.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .servers(Collections.singletonList(new Server().url("http://localhost:8000")))
                .info(new Info()
                        .title("URL Shortener API")
                        .description("Reference for building a RESTful API with Spring Boot and SpringDoc")
                        .termsOfService("terms")
                        .contact(new Contact().email("@rrajesh1979"))
                        .license(new License().name("MIT").url("https://github.com/rrajesh1979/java-spring-mongo-rest-ref/blob/master/LICENSE"))
                        .version("1.0")
                );
    }
}
