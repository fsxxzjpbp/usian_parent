package com.usian.service;

import com.usian.pojo.TbContentCategory;

import java.util.List;

public interface ContentCategoryService {

    /**
     * 内容分类管理查询
     *
     * @param id 父级Id
     * @return List
     */
    List<TbContentCategory> selectContentCategoryByParentId(Long id);
}
