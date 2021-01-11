package com.usian.controller;

import com.usian.pojo.TbItemParam;
import com.usian.service.ItemParamService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service//itemParam")
public class ItemParamController {
    @Autowired
    private ItemParamService itemParamService;

    /**
     * 查询商品规格参数
     *
     * @param itemCatId
     * @return
     */
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId) {
        return itemParamService.selectItemParamByItemCatId(itemCatId);
    }

    /**
     * 查询商品规格参数列表
     *
     * @param page 当前页
     * @param rows 每页的记录数
     * @return
     */
    @RequestMapping("/selectItemParamAll")
    PageResult selectItemParamAll(@RequestParam Integer page, @RequestParam Integer rows) {
        return itemParamService.selectItemParamAll(page, rows);
    }


    @RequestMapping("/insertItemParam")
    Integer insertItemParam(Long itemCatId, String paramData) {
        return itemParamService.insertItemParam(itemCatId, paramData);
    }

    @RequestMapping("/deleteItemParamById")
    Integer deleteItemParamById(@RequestParam Long id) {
        return itemParamService.deleteItemParamById(id);
    }
}
