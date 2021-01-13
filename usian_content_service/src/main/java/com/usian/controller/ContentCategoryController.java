package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/contentCategory")
public class ContentCategoryController {
    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        return contentCategoryService.selectContentCategoryByParentId(id);
    }

    @RequestMapping("/insertContentCategory")
    Integer insertContentCategory(@RequestBody TbContentCategory tbContentCategory) {
        return contentCategoryService.insertContentCategory(tbContentCategory);
    }

    @RequestMapping("/deleteContentCategoryById")
    Integer deleteContentCategoryById(Long categoryId) {
        return contentCategoryService.deleteContentCategoryById(categoryId);
    }

    @RequestMapping("/updateContentCategory")
    Integer updateContentCategory(@RequestBody TbContentCategory tbContentCategory) {
        return contentCategoryService.updateContentCategory(tbContentCategory);
    }

    @RequestMapping("/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(Long categoryId, Integer page, Integer rows) {
        return contentCategoryService.selectTbContentAllByCategoryId(categoryId, page, rows);
    }

    @RequestMapping("/insertTbContent")
    Integer insertTbContent(@RequestBody TbContent tbContent) {
        return contentCategoryService.insertTbContent(tbContent);
    }

    @RequestMapping("/deleteContentByIds")
    Integer deleteContentByIds(Long ids) {
        return contentCategoryService.deleteContentByIds(ids);
    }
}
