package com.usian.mq;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQSender implements ReturnCallback, ConfirmCallback {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    /**
     * 发送消息
     *
     * @param localMessage
     */
    public void sendMsg(LocalMessage localMessage) {
        RabbitTemplate amqpTemplate = (RabbitTemplate) this.amqpTemplate;
        amqpTemplate.setConfirmCallback(this);
        amqpTemplate.setReturnCallback(this);


        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());
        amqpTemplate.convertAndSend("order_exchange", "order.add", JsonUtils.objectToJson(localMessage), correlationData);
    }

    /**
     * 发送消息成功
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 手动确认为true
        if (ack) {
            // 消息发送成功,更新本地消息为已成功发送状态或者直接删除该本地消息记录
            LocalMessage localMessage = new LocalMessage();
            String id = correlationData.getId();
            localMessage.setTxNo(id);
            localMessage.setState(1);
            localMessageMapper.updateByPrimaryKeySelective(localMessage);
        }
    }

    /**
     * 发送消息失败
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("return--message:" + new String(message.getBody())
                + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }


}