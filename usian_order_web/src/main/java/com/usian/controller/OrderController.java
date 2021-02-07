package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frontend/order")
public class OrderController {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @RequestMapping("/goSettlement")
    public Result goSettlement(String[] ids, String userId, String token) {
        Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
        List<TbItem> tbItemList = new ArrayList<>();
        for (String id : ids) {
            tbItemList.add(cart.get(id));
        }
        if (tbItemList.size() > 0) {
            return Result.ok(tbItemList);
        }
        return Result.error("error");
    }

    /**
     * 提交订单
     *
     * @param orderItem       订单商品的详细信息 前台以JSON的形式传递的，里面是一个集合，可能包含多个商品的信息
     * @param tbOrderShipping 订单的物流信息，地址，用户的一些信心之类的
     * @param tbOrder         订单的一些信息，订单号，订单状态，创建时间之类的
     * @return 返回一个Result
     */
    @RequestMapping("/insertOrder")
    public Result insertOrder(String orderItem, TbOrderShipping tbOrderShipping, TbOrder tbOrder) {
        // feign只能传递一个@RequestBody的请求体，所以定义一个实体类存储上面三个参数
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTbOrder(tbOrder);
        orderInfo.setTbOrderShipping(tbOrderShipping);
        orderInfo.setOrderItem(orderItem);
        String orderId = orderServiceFeign.insertOrder(orderInfo);
        if (orderId != null) {
            return Result.ok(orderId);
        }
        return Result.error("提交订单失败！");
    }
}
