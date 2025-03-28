package org.thluon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AMQPMessageSender {
    private final Exchange apiGatewayExchange;
    private final AmqpTemplate amqpTemplate;
    public void abort(String correlationId,String exchange,String serviceName) {
        System.out.println("Aborting task "+correlationId);
        amqpTemplate.convertAndSend(exchange,serviceName+".abort",correlationId);
    }
    public String sendMessage(String message, String exchange, String routingKey) {
        String correlationId = UUID.randomUUID().toString();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("response-type","ResponseEntity");
        messageProperties.setReplyTo(apiGatewayExchange.getName()+":api-gateway.reply.http-request");
        messageProperties.setCorrelationId(correlationId);
        Message requestMessage = new Message(message.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(exchange,routingKey,requestMessage);
        return correlationId;
    }
}
