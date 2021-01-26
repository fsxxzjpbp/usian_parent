package com.usian.controller;

import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户注册与登录
 */
@RestController
@RequestMapping("/frontend/sso")
public class SSOController {

    @Autowired
    private SSOServiceFeign ssoServiceFeign;

    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result selectItemCategoryAll(@PathVariable String checkValue, @PathVariable int checkFlag) {
        Boolean selectItemCategoryAll = ssoServiceFeign.selectItemCategoryAll(checkValue, checkFlag);
        if (selectItemCategoryAll) {
            return Result.ok();
        }
        return Result.error("用户名或手机号已存在！");
    }

    @RequestMapping("/userRegister")
    public Result userRegister(TbUser tbUser) {
        Integer userRegister = ssoServiceFeign.userRegister(tbUser);
        if (userRegister == 1) {
            return Result.ok();
        }
        return Result.error("用户注册失败！");
    }

    @RequestMapping("/userLogin")
    public Result userLogin(String username, String password) {
        Map userLogin = ssoServiceFeign.userLogin(username, password);
        if (userLogin != null && userLogin.size() == 3) {
            return Result.ok(userLogin);
        }
        return Result.error("用户登录失败！");
    }
}