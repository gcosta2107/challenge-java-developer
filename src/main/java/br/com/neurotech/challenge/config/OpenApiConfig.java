package br.com.neurotech.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Challenge Java Developer API")
                        .version("1.0")
                        .description("API para avaliação de modalidades diferentes de crédito a clientes PF."));
    }
}
