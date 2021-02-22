package com.usian.service;

import com.usian.mapper.*;
import com.usian.mq.MQSender;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Autowired
    private MQSender mqSender;

    @Override
    public String insertOrder(OrderInfo orderInfo) {
        // 先获取orderInfo对象中的值
        TbOrder tbOrder = orderInfo.getTbOrder();
        String orderItem = orderInfo.getOrderItem();
        TbOrderShipping tbOrderShipping = orderInfo.getTbOrderShipping();

        // 保存订单    需要我们补充一点信息
        // 先判断redis中是否存在这个订单ID的key
        if (!redisClient.exists(ORDER_ID_KEY)) {
            redisClient.set(ORDER_ID_KEY, ORDER_ID_BEGIN);
        }
        // 先生成订单ID  订单ID需要看得懂，而且是纯数字
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1L);
        tbOrder.setOrderId(orderId.toString());
        tbOrder.setUpdateTime(new Date());
        tbOrder.setCreateTime(new Date());
        tbOrder.setStatus(1);
        tbOrderMapper.insertSelective(tbOrder);

        // 保存订单的详情
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShipping.setCreated(new Date());
        tbOrderShippingMapper.insertSelective(tbOrderShipping);

        // 保存订单的物流信息
        // 将Json转化成一个list集合  json是这种形式的[{},{},{},.....]
        List<TbOrderItem> tbOrderItemList = JsonUtils.jsonToList(orderItem, TbOrderItem.class);
        // 先判断redis中是否含有订单商品明细的Id
        if (!redisClient.exists(ORDER_ITEM_ID_KEY)) {
            redisClient.set(ORDER_ITEM_ID_KEY, 0);
        }
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            // 自增长生成一个订单商品的ID
            Long orderItemId = redisClient.incr(ORDER_ITEM_ID_KEY, 1L);
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItem.setId(orderItemId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }
        // 创建本地消息表，记录上游本地消息是否发送成功
        LocalMessage localMessage = new LocalMessage();
        localMessage.setTxNo(UUID.randomUUID().toString());
        localMessage.setState(0);
        localMessage.setOrderNo(orderId.toString());
        localMessageMapper.insertSelective(localMessage);
        /*// 发送消息到item端减库存
        amqpTemplate.convertAndSend("order_exchange", "order.add", orderId);*/
        mqSender.sendMsg(localMessage);

        return orderId.toString();
    }

    @Override
    public List<TbOrder> selectTimeOutOrder() {
        return tbOrderMapper.selectTimeOutOrder();
    }

    @Override
    public void closeTimeOutOrder(TbOrder tbOrder) {
        tbOrder.setStatus(6);
        tbOrder.setCloseTime(new Date());
        tbOrder.setUpdateTime(new Date());
        tbOrder.setEndTime(new Date());
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    @Override
    public void updateTbItemByOrderId(String orderId) {
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        tbOrderItemExample.createCriteria().andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum() + tbOrderItem.getNum());
            tbItem.setUpdated(new Date());
            tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
    }
}
