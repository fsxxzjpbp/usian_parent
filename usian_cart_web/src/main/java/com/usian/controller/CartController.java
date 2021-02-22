package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/frontend/cart")
public class CartController {

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @RequestMapping("/addItem")
    public Result addItem(String userId, Long itemId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(userId)) {
                /*----------------未登录状态添加购物车--------------*/
                // 先从cookie中查询购物车
                Map<String, TbItem> cart = getCartFromCookie(request);
                // 添加商品到购物车
                addItem2Cart(cart, itemId, num);
                // 添加购物车到cookie
                updateCart2Cookie(cart, request, response);
            } else {
                /*----------------登录状态添加购物车--------------*/
                // 从redis中查询购物车
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                // 添加商品到购物车
                addItem2Cart(cart, itemId, num);
                // 添加购物车到redis
                cartServiceFeign.addCart2Redis(userId, cart);
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("添加商品到购物车失败！");
    }

    private void updateCart2Cookie(Map<String, TbItem> cart, HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.setCookie(request,
                response,
                CART_COOKIE_KEY,
                JsonUtils.objectToJson(cart),
                CART_COOKIE_EXPIRE,
                true);
    }

    private void addItem2Cart(Map<String, TbItem> cart, Long itemId, Integer num) {
        // 查询购物车里面是否有该商品
        TbItem tbItem = cart.get(itemId.toString());
        if (tbItem != null) {
            // 购物车中已存在，直接在数量上+num
            tbItem.setNum(tbItem.getNum() + num);
        } else {
            // 不存在，先查出来，设置适量为num
            tbItem = itemServiceFeign.selectItemInfo(itemId);
            tbItem.setNum(num);
        }
        cart.put(itemId.toString(), tbItem);
    }

    private Map<String, TbItem> getCartFromCookie(HttpServletRequest request) {
        // 先从cookie中取出购物车
        String cartJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);
        // 如果购物车不为空 即购物车存在
        if (StringUtils.isNotBlank(cartJson)) {
            // 直接返回
            return JsonUtils.jsonToMap(cartJson, TbItem.class);
        }
        // cookie不存在购物车
        return new HashMap<>();
    }


    @RequestMapping("/showCart")
    public Result showCart(String userId, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<TbItem> itemArrayList = new ArrayList<>();
            if (StringUtils.isBlank(userId)) {
                /*-----------未登录-------------------*/
                Map<String, TbItem> cart = getCartFromCookie(request);
                Set<String> keySet = cart.keySet();
                for (String key : keySet) {
                    TbItem tbItem = cart.get(key);
                    itemArrayList.add(tbItem);
                }
            } else {
                // 登录状态
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                // 商品同步
                Map<String, TbItem> cartFromCookie = getCartFromCookie(request);
                if (cartFromCookie != null && cartFromCookie.size() > 0) {
                    for (String cookie : cartFromCookie.keySet()) {
                        for (String redis : cart.keySet()) {
                            if (cart.get(redis) == cartFromCookie.get(cookie)) {
                                TbItem tbItem = cart.get(redis);
                                TbItem tbItem1 = cartFromCookie.get(cookie);
                                tbItem.setNum(tbItem.getNum() + tbItem1.getNum());
                            }
                            cart.put(cartFromCookie.get(cookie).getId().toString(), cartFromCookie.get(cookie));
                        }
                    }
                }
                //清除Cook中的数据,避免重复同步
                CookieUtils.deleteCookie(request, response, CART_COOKIE_KEY);
                // 添加购物车到redis中
                cartServiceFeign.addCart2Redis(userId, cart);
                Set<String> keySet = cart.keySet();
                for (String key : keySet) {
                    TbItem tbItem = cart.get(key);
                    itemArrayList.add(tbItem);
                }
            }
            return Result.ok(itemArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("查询购物车失败！");
    }

    @RequestMapping("/updateItemNum")
    public Result updateItemNum(Integer num, String userId, Long itemId, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(userId)) {
                /*-----------未登录--------------------*/
                Map<String, TbItem> cart = getCartFromCookie(request);
                TbItem tbItem = cart.get(itemId.toString());
                tbItem.setNum(num);
                cart.put(itemId.toString(), tbItem);
                updateCart2Cookie(cart, request, response);
            } else {
                // 登录
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                TbItem tbItem = cart.get(itemId.toString());
                tbItem.setNum(num);
                cart.put(itemId.toString(), tbItem);
                cartServiceFeign.addCart2Redis(userId, cart);
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败！");
    }

    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(Long itemId, String userId, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(userId)) {
                /*-----------------未登录-------------------*/
                Map<String, TbItem> cart = getCartFromCookie(request);
                cart.remove(itemId.toString());
                updateCart2Cookie(cart, request, response);
            } else {
                // 登录
                Map<String, TbItem> cart = cartServiceFeign.getCartFromRedis(userId);
                cart.remove(itemId.toString());
                cartServiceFeign.addCart2Redis(userId, cart);
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除商品失败！");
    }
}
