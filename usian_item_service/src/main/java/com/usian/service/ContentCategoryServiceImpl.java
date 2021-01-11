package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class ContentCategoryServiceImpl  implements ContentCategoryService {

    @Resource
    private TbContentCategoryMapper tbContentCategoryMapper;



}
