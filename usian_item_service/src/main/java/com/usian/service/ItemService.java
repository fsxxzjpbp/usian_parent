package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

import java.util.Map;

public interface ItemService {

    TbItem selectItemInfo(Long itemId);

    /**
     * 查询商品列表信息
     *
     * @param page 当前页
     * @param rows 每页几条记录
     * @return PageResult
     */
    PageResult selectTbItemAllByPage(Integer page, Integer rows);

    /**
     * 新增商品
     *
     * @param tbItem     商品信息
     * @param desc       描述
     * @param itemParams 规格
     * @return Integer
     */
    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    /**
     * 删除商品
     *
     * @param itemId 商品Id
     * @return Integer
     */
    Integer deleteItemById(Long itemId);

    /**
     * 查询商品信息
     *
     * @param itemId 商品的ID
     * @return tbItem
     */
    Map<String, Object> preUpdateItem(Long itemId);

    /**
     * 修改商品
     *
     * @param tbItem     商品信息
     * @param desc       描述
     * @param itemParams 规格
     * @return Integer
     */
    Integer updateTbItem(TbItem tbItem, String desc, String itemParams);
}
