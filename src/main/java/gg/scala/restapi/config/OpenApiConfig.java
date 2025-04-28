package gg.scala.restapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scala REST API")
                        .description("REST API for Scala platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Scala Dev Team")
                                .email("support@scala.gg")
                                .url("https://scala.gg"))
                        .license(new License()
                                .name("Scala")
                                ))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.scala.gg")
                                .description("Production Server")
                ));
    }
} 