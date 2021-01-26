package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {
    @RequestMapping("/service/sso/selectItemCategoryAll")
    Boolean selectItemCategoryAll(@RequestParam String checkValue, @RequestParam int checkFlag);

    @RequestMapping("/service/sso/userRegister")
    Integer userRegister(TbUser tbUser);

    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username, @RequestParam String password);
}