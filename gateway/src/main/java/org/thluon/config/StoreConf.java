package org.thluon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StoreConf {
    // Sink store (ResponseEntity)
    @Bean
    public Map<String, Sinks.One<ResponseEntity<?>>> controllerStore() {
        return new ConcurrentHashMap<>();
    }
}
