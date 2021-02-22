package com.usian.Job;


import com.usian.mapper.LocalMessageMapper;
import com.usian.mq.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.LocalMessageExample;
import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Value("${SETNX_LOCK_ORDER_KEY}")
    private String SETNX_LOCK_ORDER_KEY;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Autowired
    private MQSender mqSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //解决quartz集群任务重复执行问题
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(redisClient.setnx(SETNX_LOCK_ORDER_KEY,ip,30)){
            //1、查询超时订单
            List<TbOrder> tbOrderList = orderService.selectTimeOutOrder();

            //2、关闭超时订单
            for (TbOrder tbOrder : tbOrderList) {
                orderService.closeTimeOutOrder(tbOrder);
                //3、把超时订单中的商品库存数量加回去
                orderService.updateTbItemByOrderId(tbOrder.getOrderId());
            }

            //扫描本地消息表
            LocalMessageExample localMessageExample = new LocalMessageExample();
            localMessageExample.createCriteria().andStateEqualTo(1);
            List<LocalMessage> localMessageList = localMessageMapper.selectByExample(localMessageExample);
            for (LocalMessage localMessage : localMessageList) {
                mqSender.sendMsg(localMessage);
            }
            //发送消息
            redisClient.del(SETNX_LOCK_ORDER_KEY);
        }else{
            System.out.println("=================任务正在执行=======================");
        }
    }
}
