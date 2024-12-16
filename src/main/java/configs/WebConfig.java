package configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(org.springframework.web.reactive.config.CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:3000")  // Specify the client URL
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
    }
}
