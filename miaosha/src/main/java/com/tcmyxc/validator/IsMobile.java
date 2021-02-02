package com.tcmyxc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 徐文祥
 * @date 2021/1/14 23:10
 */

// 这几个注解从 @Length 注解里面复制粘贴的
@Documented
@Constraint(validatedBy = { IsMobileValidator.class })// 说明这个注解是调用哪个类校验的
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface IsMobile {

    boolean required() default true;// 默认必须要有

    String message() default "{手机号码格式有误}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
