package com.tcmyxc.config;

import com.tcmyxc.access.UserContext;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这个类解决了每个方法在调用之前需要判断用户的 session 是否过期的判断
 * 用户参数解析器
 * @author 徐文祥
 * @date 2021/1/15 3:23
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();// 获取参数的类型
        return clazz == MiaoshaUser.class;// 如果是秒杀的用户才做处理
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();// 从线程本地变量里面取用户
    }
}
