package com.tcmyxc.validator;

import com.tcmyxc.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * IsMobile 注解校验器
 * @author 徐文祥
 * @date 2021/1/14 23:28
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;// 默认不需要

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        // 初始化方法可以拿到注解
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 判断值是否是必须的
        if(required){
            // 判断手机号的格式
            return ValidatorUtil.isMobile(value);
        }
        else {
            // 判断值是否合法
            if (StringUtils.isEmpty(value)){
                return true;
            }
            else {
                // 判断手机号的格式
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
