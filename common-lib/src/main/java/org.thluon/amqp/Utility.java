package org.thluon.amqp;

@SuppressWarnings("unused")
public class Utility {
    /**
     * @see org.thluon.amqp.ReplyTo
     */
    public static ReplyTo extractReplyTo(String replyTo) {
        String[] replyToParts = replyTo.split(":", 2);
        if(replyToParts.length != 2)
            return new ReplyTo("",replyToParts[0]);
        return new ReplyTo(replyToParts[0],replyToParts[1]);
    }

    /**
     * @see org.thluon.amqp.ReplyTo
     */
    public static String buildReplyTo(ReplyTo replyTo) {
        return replyTo.exchange() + ":" + replyTo.routingKey();
    }
}
