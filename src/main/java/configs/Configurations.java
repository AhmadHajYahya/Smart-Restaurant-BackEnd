package configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Configurations {

	@Bean
	public MappingJackson2HttpMessageConverter octetStreamJsonConverter() {
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setSupportedMediaTypes(List.of(new MediaType("application", "octet-stream")));
	    return converter;
	}
	
	@Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
