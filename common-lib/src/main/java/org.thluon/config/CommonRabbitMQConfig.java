package org.thluon.config;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centralize RabbitMQ configuration. (i.e. Exchange, Queue, etc.)
 */
@SuppressWarnings("unused")
@Configuration
public class CommonRabbitMQConfig {

    @Bean
    public Exchange apiGatewayExchange() {
        return new DirectExchange("api-gateway-exchange");
    }

    @Bean
    public Exchange itemExchange() {
        return new DirectExchange("item-exchange");
    }

    @Bean
    public Queue gatewayQueue() {
        return new Queue("gatewayQueue");
    }

    @Bean
    public Queue itemQueue() {
        return new Queue("itemQueue", true);
    }
    @Bean
    public Queue itemAbortQueue() {
        return new Queue("itemAbortQueue", true);
    }
}