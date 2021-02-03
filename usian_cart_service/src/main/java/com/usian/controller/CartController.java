package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/getCartFromRedis")
    Map<String, TbItem> getCartFromRedis(String userId) {
        return cartService.getCartFromRedis(userId);
    }

    @RequestMapping("/addCart2Redis")
    void addCart2Redis(String userId, @RequestBody Map<String, TbItem> cart) {
        cartService.addCart2Redis(userId, cart);
    }
}
