package com.tcmyxc.service;

import com.tcmyxc.dao.MiaoshaUserDao;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.exception.GlobalException;
import com.tcmyxc.exception.GlobalExceptionHandlder;
import com.tcmyxc.redis.MiaoshaUserKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.util.MD5Util;
import com.tcmyxc.util.UUIDUtil;
import com.tcmyxc.vo.LoginVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 徐文祥
 * @date 2021/1/13 23:12
 */

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public boolean updatePassword(String token, long id, String newPassword){
        // 取user
        MiaoshaUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXITS);
        }
        // 更新密码
        MiaoshaUser toBeUpdateUser = new MiaoshaUser();
        toBeUpdateUser.setId(id);
        toBeUpdateUser.setPassword(MD5Util.formPassToDBPass(newPassword, user.getSalt()));
        miaoshaUserDao.update(toBeUpdateUser);
        // 处理有关缓存，和这个对象有关的缓存都需要修改，不然会出现数据不一致
        redisService.delete(MiaoshaUserKey.getById, "" + id);
        user.setPassword(toBeUpdateUser.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    // 根据 ID 获取用户，对象级缓存
    public MiaoshaUser getById(long id){
        // 取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if(user != null){
            return user;
        }
        // 缓存中没有，从数据库中查找
        user = miaoshaUserDao.getById(id);
        // 放到缓存里面
        if(user != null){
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
    }

    // 根据用户的 token 获取用户信息
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        // 参数校验
        if(StringUtils.isEmpty(token)){
            return null;
        }
        // 从 redis 缓存中取用户
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if(user != null){
            // 延长 cookie 有效期
            addCookie(response, token, user);
        }
        return user;
    }

    // 用户发起登录请求之后的一些判断操作，以及 cookie 的一些操作
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();// 从登录表单获取的经过加密的密码
        MiaoshaUser user = getById(Long.parseLong(mobile));// 根据手机号从数据库查询用户
        // 判断手机号是否已注册
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXITS);
        }
        // 验证输入密码是否正确
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String formPassToDBPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if(!dbPass.equals(formPassToDBPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 生成 cookie
        String token = UUIDUtil.uuid();// 生成一个令牌
        addCookie(response, token, user);
        return token;
    }

    // 添加 cookie 封装成函数
    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user){
        // 生成 cookie
        redisService.set(MiaoshaUserKey.token, token, user);// 将 token 写进 redis
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);// 将 token 存进 cookie
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());// cookie 有效期和 token 一致
        cookie.setPath("/");// 设置存放目录
        response.addCookie(cookie);// 把 cookie 放进响应里面
    }
}
