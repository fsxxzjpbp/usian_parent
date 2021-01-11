package com.usian.service;

import com.usian.mapper.TbOrderItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class OrderItemServiceImpl  implements OrderItemService {

    @Resource
    private TbOrderItemMapper tbOrderItemMapper;



}
