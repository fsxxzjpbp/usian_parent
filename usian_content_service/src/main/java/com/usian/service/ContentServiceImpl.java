package com.usian.service;

import com.usian.mapper.TbContentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class ContentServiceImpl  implements ContentService {

    @Resource
    private TbContentMapper tbContentMapper;


}
