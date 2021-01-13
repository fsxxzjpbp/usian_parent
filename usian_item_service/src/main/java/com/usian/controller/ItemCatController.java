package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.service.ItemCatService;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemCategory")
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;

    /**
     * 查询商品类目
     *
     * @param id 父类目ID=0时，代表的是一级的类目
     * @return List<TbItemCat>
     */
    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        return itemCatService.selectItemCategoryByParentId(id);
    }

    @RequestMapping("/selectItemCategoryAll")
    CatResult selectItemCategoryAll() {
        return itemCatService.selectItemCategoryAll();
    }
}
