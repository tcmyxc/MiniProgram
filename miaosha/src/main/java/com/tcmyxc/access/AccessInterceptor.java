package com.tcmyxc.access;

import com.alibaba.fastjson.JSON;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.redis.MiaoshaKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 徐文祥
 * @date 2021/1/30 21:38
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 如果是 HandlerMethod
        if(handler instanceof HandlerMethod){
            // 取用户
            MiaoshaUser user = getUser(request, response);
            UserContext.setUser(user);// 在线程本地变量里设置用户
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){// 如果方法上没有注解，那就什么就不用做
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                // 如果需要登录，则需要验证用户是否已登录
                if(user == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }
            else{
                // 啥也不干
            }

            // 接口防刷限流
            MiaoshaKey accessKey = MiaoshaKey.withExpire(seconds);

            Integer cnt = redisService.get(accessKey, key, Integer.class);
            if(cnt == null){
                redisService.set(accessKey, key, 1);
            }
            else if(cnt < maxCount){
                redisService.incr(accessKey, key);
            }
            else{
                render(response, CodeMsg.ACCESS_LIMITED);
                return false;
            }
        }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");// 不加这一句前端会乱码
        ServletOutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
        // 参数判断，如果 cookie 已过期，返回登录页面
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);// 获取用户信息
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        // 从请求里面遍历所有的 cookie
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
