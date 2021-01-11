package com.usian.service;

import com.usian.mapper.TbItemParamItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class ItemParamItemServiceImpl  implements ItemParamItemService {

    @Resource
    private TbItemParamItemMapper tbItemParamItemMapper;



}
