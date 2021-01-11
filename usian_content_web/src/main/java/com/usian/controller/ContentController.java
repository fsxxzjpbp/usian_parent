package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequestMapping("/backend/content")
public class ContentController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Integer id) {
        List<TbContentCategory> tbContentCategoryList = contentServiceFeign.selectContentCategoryByParentId(id);
        if (tbContentCategoryList != null && tbContentCategoryList.size() > 0) {
            return Result.ok(tbContentCategoryList);
        }
        return Result.error("查询内容分类失败！");
    }
}
