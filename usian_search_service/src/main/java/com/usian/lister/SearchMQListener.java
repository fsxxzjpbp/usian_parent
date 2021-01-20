package com.usian.lister;

import com.rabbitmq.client.Channel;
import com.usian.service.SearchItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchMQListener {
    @Autowired
    private SearchItemService searchItemService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue", durable = "true"),
            exchange = @Exchange(value = "item_exchange", type = ExchangeTypes.TOPIC),
            key = {"item.*"}
    ))
    public void listen(String msg, Channel channel, Message message) throws IOException {
        int result = searchItemService.insertDocument(msg);
        if (result > 0) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
