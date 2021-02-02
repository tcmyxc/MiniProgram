package com.tcmyxc.config;

import com.tcmyxc.access.AccessInterceptor;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 这个类我们使用最新的特性，不使用废弃的抽象类 WebMvcConfigurerAdapter
 * @author 徐文祥
 * @date 2021/1/15 3:16
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    // 注册自定义的参数解析器
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
