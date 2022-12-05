package org.rrajesh1979.urlshort.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("URL Shortener API")
                        .description("Reference for building a RESTful API with Spring Boot and SpringDoc")
                        .termsOfService("terms")
                        .contact(new Contact().email("@rrajesh1979"))
                        .license(new License().name("MIT"))
                        .version("1.0")
                );
    }
}
