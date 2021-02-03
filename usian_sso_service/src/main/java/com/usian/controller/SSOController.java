package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户注册与登录
 */
@RestController
@RequestMapping("/service/sso")
public class SSOController {
    @Autowired
    private SSOService ssoService;

    @RequestMapping("/selectItemCategoryAll")
    Boolean selectItemCategoryAll(String checkValue, int checkFlag) {
        return ssoService.selectItemCategoryAll(checkValue, checkFlag);
    }

    @RequestMapping("/userRegister")
    Integer userRegister(@RequestBody TbUser tbUser) {
        return ssoService.userRegister(tbUser);
    }

    @RequestMapping("/userLogin")
    Map userLogin(String username, String password) {
        return ssoService.userLogin(username, password);
    }

    @RequestMapping("/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token) {
        return ssoService.getUserByToken(token);
    }

    @RequestMapping("/logOut")
    Boolean logOut( String token) {
        return ssoService.logOut(token);
    }
}