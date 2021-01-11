package com.usian.service;

import com.usian.mapper.TbItemDescMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class ItemDescServiceImpl  implements ItemDescService {

    @Resource
    private TbItemDescMapper tbItemDescMapper;



}
