package com.tcmyxc.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 徐文祥
 * @date 2021/1/30 21:35
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int seconds();
    int maxCount();
    boolean needLogin() default false;
}
