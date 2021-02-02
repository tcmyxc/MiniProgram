package com.tcmyxc.controller;

import com.tcmyxc.domain.User;
import com.tcmyxc.rabbitmq.MQSender;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.redis.UserKey;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tcmyxc
 * @date 2021/1/10 22:18
 */
@Controller
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @GetMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        mqSender.send("hello tcmyxc");
        return Result.success("恭喜，成功");
    }

    @GetMapping("/mq/topic")
    @ResponseBody
    public Result<String> topicMq() {
        mqSender.sendTopic("hello tcmyxc");
        return Result.success("恭喜，成功");
    }

    @GetMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanoutMq() {
        mqSender.sendFanout("hello tcmyxc");
        return Result.success("恭喜，成功");
    }

    @GetMapping("/mq/header")
    @ResponseBody
    public Result<String> headerMq() {
        mqSender.sendHeader("hello tcmyxc");
        return Result.success("恭喜，成功");
    }

    @GetMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("恭喜，成功");
    }

    @GetMapping("/error")
    @ResponseBody
    public Result<String> helloerror() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @GetMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "hhh");
        return "index";
    }

    @GetMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getUserById(1);
        return Result.success(user);
    }

    @GetMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTransaction() {
        userService.tx();
        return Result.success(true);
    }

    @GetMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "1", User.class);
        return Result.success(user);
    }

    @GetMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User(1, "1234");
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(true);
    }
}
