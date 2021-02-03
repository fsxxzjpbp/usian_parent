package com.usian.service;

import com.usian.pojo.TbItem;

import java.util.Map;

public interface CartService {
    Map<String, TbItem> getCartFromRedis(String userId);

    void addCart2Redis(String userId, Map<String, TbItem> cart);
}
