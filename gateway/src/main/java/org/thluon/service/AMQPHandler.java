package org.thluon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AMQPHandler {
    private final ObjectMapper objectMapper ;
    private final Map<String, Sinks.One<ResponseEntity<?>>> controllerStore;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "#{gatewayQueue.getActualName()}", durable = "true"),
                    exchange = @Exchange(value = "#{apiGatewayExchange.getName()}", ignoreDeclarationExceptions = "true"),
                    key = "api-gateway.reply.http-request"
            )
    )
    public void handleAmqpReply(Message message) {
        byte[] body = message.getBody();
        String correlationId = message.getMessageProperties().getCorrelationId();
        System.out.println("response for task "+correlationId);
        Sinks.One<ResponseEntity<?>> sink = controllerStore.get(correlationId);
        if (sink != null) {

            if (Objects.equals(message.getMessageProperties().getHeaders().get("response-type"), "ResponseEntity")) {
                ResponseEntity<?> response;
                try {
                     response =  objectMapper.readValue(body, new TypeReference<>() {});
                } catch (IOException e) {
                    e.printStackTrace();
                     response = ResponseEntity.internalServerError().body("Fail to deserialize ResponseEntity");
                }
                System.out.println("Response with Object");
                sink.tryEmitValue(response);
            }else{
                System.out.println("Response with string");
                sink.tryEmitValue(ResponseEntity.ok(new String(body, StandardCharsets.UTF_8)));
            }
        }
        else{
            System.out.println("sink not found for task "+correlationId);
        }
    }
}
