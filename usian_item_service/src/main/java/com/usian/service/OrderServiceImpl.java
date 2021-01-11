package com.usian.service;

import com.usian.mapper.TbOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private TbOrderMapper tbOrderMapper;


}
