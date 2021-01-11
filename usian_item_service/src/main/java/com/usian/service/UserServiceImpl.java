package com.usian.service;

import com.usian.mapper.TbUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private TbUserMapper tbUserMapper;

}
