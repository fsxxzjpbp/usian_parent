package com.usian.interceptor;

import com.usian.feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private SSOServiceFeign ssoServiceFeign;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 查看redis中是否有，需要cookie中的token
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            return false;
        }
        TbUser tbUser = ssoServiceFeign.getUserByToken(token);
        if (tbUser != null) {
            return true;
        }
        return false;
    }
}
