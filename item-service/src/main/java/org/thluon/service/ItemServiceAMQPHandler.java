package org.thluon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thluon.amqp.ReplyTo;
import org.thluon.amqp.Utility;
import org.thluon.thread.MaxCachedThreadPool;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Example of a AMQP Handler.
 * Listening for two queues:
 * <ul>
 *     <li>itemQueue: {@link org.thluon.config.CommonRabbitMQConfig#itemQueue}</li>
 *     <li>itemAbortQueue: {@link org.thluon.config.CommonRabbitMQConfig#itemAbortQueue}</li>
 * </ul>
 * <br>
 * <ol>
 *     <li>itemQueue:
 *         <ul>
 *             <li>Processing (heavy task) get all items</li>
 *             <li>Reply with fetched data and set header metadata</li>
 *         </ul>
 *     </li>
 *     <li>itemAbortQueue:
 *         <ul>
 *             <li>Abort task with id</li>
 *         </ul>
 *     </li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ItemServiceAMQPHandler {
    private final ItemService itemService;

    // Max worker thread (excluding abort worker).
    private static final int MAX_POOL_SIZE = 3;
    // Thread that use to abort a task (by UUID) on request
    private static final ExecutorService abortWorker = Executors.newFixedThreadPool(1);
    // Worker threads
    private static final ExecutorService worker = new MaxCachedThreadPool(
            MAX_POOL_SIZE,
            500L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()
    );

    private static final Map<String, Future<?>> taskMap = new ConcurrentHashMap<>();
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Listener which will handle the message.
     * Expect to have a message with a correlationId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "#{itemQueue.getActualName()}", durable = "true"),
                    exchange = @Exchange(value = "#{itemExchange.getName()}", ignoreDeclarationExceptions = "true"),
                    key = "item.all"
            )
    )
    public void getItems(Message message) throws RuntimeException {
        String taskId = message.getMessageProperties().getCorrelationId();
        if (taskId == null) {
            System.out.println("NULL TASK ID");
            byte[] body = message.getBody();
            System.out.println(new String(body, StandardCharsets.UTF_8));
            return;
        }
        Future<?> task = worker.submit(handleHeavyJob(message, taskId));
        taskMap.put(taskId, task);
        System.out.println("Added to task map | size: " + taskMap.size());
    }

    private Runnable handleHeavyJob(Message message, String taskId) {
        return () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // Simulate job is aborted!
                System.out.println("Interrupted!");
                return;
            }
            String correlationId = message.getMessageProperties().getCorrelationId();
            String replyToStr = message.getMessageProperties().getReplyTo();
            ReplyTo replyTo = Utility.extractReplyTo(replyToStr);
            String responseType = Objects.toString(message.getMessageProperties().getHeaders().get("response-type"), "");
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(correlationId);
            Message replyMsg;
            if (responseType.equals("ResponseEntity")) {
                ResponseEntity<?> response = ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON).body(itemService.getItems());
                byte[] body;
                try {
                    body = objectMapper.writeValueAsBytes(response);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                messageProperties.setHeader("response-type", "ResponseEntity");
                replyMsg = MessageBuilder.withBody(body).andProperties(messageProperties).build();
                System.out.println("Response with Object");
            } else {
                String replyMessage = "Get User Successfully!";
                replyMsg = MessageBuilder.withBody(replyMessage.getBytes())
                        .andProperties(messageProperties)
                        .setCorrelationId(correlationId)
                        .build();
            }
            amqpTemplate.convertAndSend(replyTo.exchange(), replyTo.routingKey(), replyMsg);
            try {
                // Remove task from map
                taskMap.remove(taskId);
            } catch (Exception e) {
            }
        };
    }

    /**
     * Try to quit a task by UUID to save resource.
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "#{itemAbortQueue.getActualName()}", durable = "true"),
                    exchange = @Exchange(value = "#{itemExchange.getName()}", ignoreDeclarationExceptions = "true"),
                    key = "item.abort"
            )
    )
    public void abortTask(Message message) {
        abortWorker.submit(() -> {
            byte[] body = message.getBody();
            String correlationId = new String(body, StandardCharsets.UTF_8);
            Future<?> task = taskMap.get(correlationId);
            System.out.println("Aborting task: " + correlationId);
            if (task != null) {
                task.cancel(true);
                taskMap.remove(correlationId);
            } else {
                System.out.println("Task not found: " + correlationId);
            }
        });
    }
}
