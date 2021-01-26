package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class SSOServiceImpl implements SSOService {

    @Resource
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Override
    public Boolean selectItemCategoryAll(String checkValue, int checkFlag) {
        TbUserExample example = new TbUserExample();
        if (checkFlag == 1) {
            // 等于1 表示用用户名注册
            example.createCriteria().andUsernameEqualTo(checkValue);
        } else if (checkFlag == 2) {
            // 等于2 表示用手机号注册
            example.createCriteria().andPhoneEqualTo(checkValue);
        }
        List<TbUser> tbUserList = tbUserMapper.selectByExample(example);
        if (tbUserList == null || tbUserList.size() == 0) {
            // 返回true表示没有该用户
            return true;
        }
        // 返回false表示有该用户，无法注册
        return false;
    }

    @Override
    public Integer userRegister(TbUser tbUser) {
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        tbUser.setPassword(MD5Utils.digest(tbUser.getPassword()));
        return tbUserMapper.insertSelective(tbUser);
    }

    @Override
    public Map userLogin(String username, String password) {
        password = MD5Utils.digest(password);
        TbUserExample example = new TbUserExample();
        example.createCriteria().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<TbUser> tbUserList = tbUserMapper.selectByExample(example);
        if (tbUserList == null || tbUserList.size() == 0) {
            return null;
        }
        TbUser tbUser = tbUserList.get(0);
        String token = UUID.randomUUID().toString();
        redisClient.set(USER_INFO + ":" + token, tbUser);
        redisClient.expire(USER_INFO + ":" + token, SESSION_EXPIRE);
        Map<String, Object> map = new HashMap();
        map.put("token", token);
        map.put("userid", tbUser.getId());
        map.put("username", username);
        return map;
    }
}
