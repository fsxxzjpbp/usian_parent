package com.usian.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.stereotype.Component;

@Component
public class MQSender implements ReturnCallback, ConfirmCallback{


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

    }
}