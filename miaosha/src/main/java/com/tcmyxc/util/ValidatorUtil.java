package com.tcmyxc.util;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 徐文祥
 * @date 2021/1/13 23:00
 */
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    // 判断手机号是否符合要求
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }

        Matcher matcher = mobile_pattern.matcher(src);
        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("123456"));
        System.out.println(isMobile("12345678901"));
    }
}
