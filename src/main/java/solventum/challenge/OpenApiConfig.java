package solventum.challenge;

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
                        .title("URL Transform API")
                        .version("1.0.0")
                        .description("""
                            This API provides endpoints to encode and decode URLs, enabling short URL generation and retrieval.
                            ## Features
                            - **Encode**: Convert a long URL into a short hash.
                            - **Decode**: Retrieve the original URL from a hash.
                            """));
    }
}