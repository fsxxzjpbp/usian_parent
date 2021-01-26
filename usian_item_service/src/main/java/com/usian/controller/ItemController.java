package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParamItem;
import com.usian.service.ItemService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/item")
public class ItemController {
    @Autowired
    private ItemService itemService;


    /**
     * 查询商品信息
     * 根据商品id
     *
     * @param itemId
     * @return Tbitem
     */
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId) {
        return this.itemService.selectItemInfo(itemId);
    }

    /**
     * 查询商品列表信息
     *
     * @param page 当前页
     * @param rows 每页几条记录
     * @return PageResult
     */
    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        return itemService.selectTbItemAllByPage(page, rows);
    }

    /**
     * 新增商品
     *
     * @param tbItem     商品信息
     * @param desc       描述
     * @param itemParams 规格
     * @return Integer
     */
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem, String desc, String itemParams) {
        return itemService.insertTbItem(tbItem, desc, itemParams);
    }

    /**
     * 删除商品
     *
     * @param itemId 商品Id
     * @return Integer
     */
    @RequestMapping("/deleteItemById")
    Integer deleteItemById(Long itemId) {
        return itemService.deleteItemById(itemId);
    }

    /**
     * 查询商品信息
     *
     * @param itemId 商品的ID
     * @return tbItem
     */
    @RequestMapping("/preUpdateItem")
    Map<String, Object> preUpdateItem(Long itemId) {
        return itemService.preUpdateItem(itemId);
    }

    /**
     * 修改商品
     *
     * @param tbItem     商品信息
     * @param desc       描述
     * @param itemParams 规格
     * @return Integer
     */
    @RequestMapping("/updateTbItem")
    Integer updateTbItem(@RequestBody TbItem tbItem, String desc, String itemParams) {
        return itemService.updateTbItem(tbItem, desc, itemParams);
    }

    @RequestMapping("/selectItemDescByItemId")
    TbItemDesc selectItemDescByItemId(Long itemId) {
        return itemService.selectItemDescByItemId(itemId);
    }

    @RequestMapping("/selectTbItemParamItemByItemId")
    TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        return itemService.selectTbItemParamItemByItemId(itemId);
    }
}