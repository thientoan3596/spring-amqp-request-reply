package org.thluon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.thluon.jackson.ResponseEntityDeserializer;

@Configuration
public class Jackson {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ResponseEntity.class, new ResponseEntityDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
