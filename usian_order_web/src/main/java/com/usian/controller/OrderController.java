package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.TbItem;
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
}
