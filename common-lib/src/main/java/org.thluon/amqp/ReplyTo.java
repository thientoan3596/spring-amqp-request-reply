package org.thluon.amqp;

/**
 * Custom record for reply to header.
 * replyTo = [{exchange}:]{routingKey}
 */
public record ReplyTo(String exchange,String routingKey) {}
