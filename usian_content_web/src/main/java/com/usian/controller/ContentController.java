package com.usian.controller;


import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/content")
public class ContentController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;

    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id) {
        List<TbContentCategory> tbContentCategoryList = contentServiceFeign.selectContentCategoryByParentId(id);
        if (tbContentCategoryList != null && tbContentCategoryList.size() > 0) {
            return Result.ok(tbContentCategoryList);
        }
        return Result.error("查询内容分类失败！");
    }

    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory) {
        Integer insertNum = contentServiceFeign.insertContentCategory(tbContentCategory);
        if (insertNum == 1) {
            return Result.ok();
        }
        return Result.error("添加内容分类失败！");
    }

    @RequestMapping("/deleteContentCategoryById")
    public Result deleteContentCategoryById(Long categoryId) {
        Integer deleteContentCategoryById = contentServiceFeign.deleteContentCategoryById(categoryId);
        if (deleteContentCategoryById == 1) {
            return Result.ok();
        }
        return Result.error("删除商品内容分类失败！");
    }

    @RequestMapping("/updateContentCategory")
    public Result updateContentCategory(TbContentCategory tbContentCategory) {
        Integer updateContentCategory = contentServiceFeign.updateContentCategory(tbContentCategory);
        if (updateContentCategory == 1) {
            return Result.ok();
        }
        return Result.error("修改内容分类失败！");
    }

    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(Long categoryId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "9") Integer rows) {
        PageResult pageResult = contentServiceFeign.selectTbContentAllByCategoryId(categoryId, page, rows);
        if (pageResult.getResult() != null && pageResult.getResult().size() > 0) {
            return Result.ok(pageResult);
        }
        return Result.error("查询内容失败！");
    }

    @RequestMapping("/insertTbContent")
    public Result insertTbContent(TbContent tbContent) {
        Integer insertTbContent = contentServiceFeign.insertTbContent(tbContent);
        if (insertTbContent == 1) {
            return Result.ok();
        }
        return Result.error("添加内容失败！");
    }

    @RequestMapping("/deleteContentByIds")
    public Result deleteContentByIds(Long ids) {
        Integer insertTbContent = contentServiceFeign.deleteContentByIds(ids);
        if (insertTbContent == 1) {
            return Result.ok();
        }
        return Result.error("删除内容失败！");
    }
}
