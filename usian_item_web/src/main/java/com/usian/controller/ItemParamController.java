package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParamController {
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 查询商品规格参数
     *
     * @param itemCatId
     * @return Result
     */
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Long itemCatId) {
        TbItemParam tbItemParam = itemServiceFeign.selectItemParamByItemCatId(itemCatId);
        if (tbItemParam != null) {
            return Result.ok(tbItemParam);
        }
        return Result.error("查询商品规格失败！");
    }

    /**
     * 查询商品规格参数
     *
     * @param page 当前页
     * @param rows 每页多少条记录
     * @return Result
     */
    @RequestMapping("/selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "3") Integer rows) {
        PageResult pageResult = itemServiceFeign.selectItemParamAll(page, rows);
        if (pageResult != null && pageResult.getResult().size() > 0) {
            return Result.ok(pageResult);
        }
        return Result.error("查询商品规格参数失败！");
    }

    @RequestMapping("/insertItemParam")
    public Result insertItemParam(Long itemCatId, String paramData) {
        Integer insertItemParam = itemServiceFeign.insertItemParam(itemCatId, paramData);
        if (insertItemParam == 1) {
            return Result.ok();
        }
        return Result.error("添加失败：该类目已有规格模板");
    }

    @RequestMapping("/deleteItemParamById")
    public Result deleteItemParamById(Long id) {
        Integer deleteItemParamById = itemServiceFeign.deleteItemParamById(id);
        if (deleteItemParamById == 1) {
            return Result.ok();
        }
        return Result.error("删除商品规格模板失败");
    }
}
