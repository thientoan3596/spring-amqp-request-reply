package org.thluon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.thluon.config.CommonRabbitMQConfig;

@SpringBootApplication
@Import(CommonRabbitMQConfig.class)
public class ItemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}
