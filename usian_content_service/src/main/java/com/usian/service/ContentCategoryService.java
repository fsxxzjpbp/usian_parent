package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.PageResult;

import java.util.List;

public interface ContentCategoryService {

    /**
     * 内容分类管理查询
     *
     * @param id 父级Id
     * @return List
     */
    List<TbContentCategory> selectContentCategoryByParentId(Long id);

    Integer insertContentCategory(TbContentCategory tbContentCategory);

    Integer deleteContentCategoryById(Long categoryId);

    Integer updateContentCategory(TbContentCategory tbContentCategory);

    PageResult selectTbContentAllByCategoryId(Long categoryId, Integer page, Integer rows);


}
