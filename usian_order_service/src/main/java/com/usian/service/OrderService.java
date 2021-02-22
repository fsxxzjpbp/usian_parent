package com.usian.service;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;

import java.util.List;

public interface OrderService {
    String insertOrder(OrderInfo orderInfo);

    List<TbOrder> selectTimeOutOrder();

    void closeTimeOutOrder(TbOrder tbOrder);

    void updateTbItemByOrderId(String orderId);
}
