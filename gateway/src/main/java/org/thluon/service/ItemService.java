package org.thluon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * A Service which will delegate job to downstream microservice (i.e., item-service)
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private final Map<String, Sinks.One<ResponseEntity<?>>> controllerStore;
    private final AMQPMessageSender AMQPMessageSender;

    /**
     * Delegating job downstream via AMQP.
     * @return sink of ResponseEntity
     */
    public Mono<ResponseEntity<?>> getItems(long timeoutSeconds){
        Sinks.One<ResponseEntity<?>> sink = Sinks.one();
        String correlationId = AMQPMessageSender.sendMessage("data","item-exchange","item.all");
        System.out.println("waiting task " +correlationId);
        controllerStore.put(correlationId, sink);
        return sink.asMono()
                // Set timeout
                .timeout(Duration.ofSeconds(timeoutSeconds))
                // Signalling downstream to abort such task!
                .doOnError(TimeoutException.class, e -> AMQPMessageSender.abort(correlationId, "item-exchange", "item"))
                // Remove from store
                .doFinally(signal -> controllerStore.remove(correlationId));
    }
    public Mono<ResponseEntity<?>> getItems(){
        return getItems(6);
    }

}
