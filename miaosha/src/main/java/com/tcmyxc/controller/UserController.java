package com.tcmyxc.controller;

import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author 徐文祥
 * @date 2021/1/17 0:37
 */


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }

}