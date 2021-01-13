package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 查询商品基本信息
     */
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId) {
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem != null) {
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    /**
     * 分页查询商品
     *
     * @param page 当前页
     * @param rows 每页多少条数据
     * @return Result
     */
    @RequestMapping("/selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "9") Integer rows) {
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page, rows);
        if (pageResult.getResult() != null && pageResult.getResult().size() > 0) {
            return Result.ok(pageResult);
        }
        return Result.error("查询商品列表失败！");
    }

    /**
     * 添加商品
     *
     * @param tbItem     商品信息
     * @param desc       商品描述
     * @param itemParams 商品规格参数
     * @return Result
     */
    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem, String desc, String itemParams) {
        Integer insertTbItem = itemServiceFeign.insertTbItem(tbItem, desc, itemParams);
        if (insertTbItem == 3) {
            return Result.ok();
        }
        return Result.error("添加商品失败！");
    }

    /**
     * 删除商品
     *
     * @param itemId 商品Id
     * @return Result
     */
    @RequestMapping("/deleteItemById")
    public Result deleteItemById(Long itemId) {
        Integer deleteItemById = itemServiceFeign.deleteItemById(itemId);
        if (deleteItemById == 1) {
            return Result.ok();
        }
        return Result.error("删除商品失败！");
    }

    /**
     * 根据商品 ID 查询商品，商品分类，商品描述，商品规格参数
     *
     * @param itemId 商品Id
     * @return Result
     */
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId) {
        Map<String, Object> map = itemServiceFeign.preUpdateItem(itemId);
        if (map.size() > 0) {
            return Result.ok(map);
        }
        return Result.error("查询商品信息失败！");
    }


    /**
     * 修改商品
     *
     * @param tbItem     商品信息
     * @param desc       商品描述
     * @param itemParams 商品规格参数
     * @return Result
     */
    @RequestMapping("/updateTbItem")
    public Result updateTbItem(TbItem tbItem, String desc, String itemParams) {
        Integer insertTbItem = itemServiceFeign.updateTbItem(tbItem, desc, itemParams);
        if (insertTbItem == 3) {
            return Result.ok();
        }
        return Result.error("修改商品失败！");
    }
}