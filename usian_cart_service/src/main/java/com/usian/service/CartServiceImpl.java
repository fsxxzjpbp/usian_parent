package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisClient redisClient;

    @Value("${CART_REDIS_KEY}")
    public String CART_REDIS_KEY;

    @Override
    public Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String, TbItem> cart = (Map<String, TbItem>) redisClient.hget(CART_REDIS_KEY, userId);
        if (cart != null && cart.size() > 0) {
            return cart;
        }
        return new HashMap<>();
    }

    @Override
    public void addCart2Redis(String userId, Map<String, TbItem> cart) {
        redisClient.hset(CART_REDIS_KEY, String.valueOf(userId), cart);
    }
}
