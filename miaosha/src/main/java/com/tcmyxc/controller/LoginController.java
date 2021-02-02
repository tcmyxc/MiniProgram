package com.tcmyxc.controller;

import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.MiaoshaUserService;
import com.tcmyxc.util.ValidatorUtil;
import com.tcmyxc.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 徐文祥
 * @date 2021/1/13 22:20
 */

@Controller
public class LoginController {

    @Autowired
    private MiaoshaUserService userService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login/do_login")
    @ResponseBody
    // 使用 JSR303 参数校验
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        // 参数校验
//        String password = loginVo.getPassword();
//        String mobile = loginVo.getMobile();
//        if(StringUtils.isEmpty(password)){
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile)){
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }

        // 执行登录请求
        String token = userService.login(response, loginVo);
        return Result.success(token);
    }
}
