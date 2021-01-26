package com.usian.service;

import com.usian.pojo.TbUser;

import java.util.Map;

public interface SSOService {

    Boolean selectItemCategoryAll(String checkValue, int checkFlag);

    Integer userRegister(TbUser tbUser);

    Map userLogin(String username, String password);
}
