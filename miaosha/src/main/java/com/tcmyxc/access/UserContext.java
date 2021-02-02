package com.tcmyxc.access;

import com.tcmyxc.domain.MiaoshaUser;

/**
 * @author 徐文祥
 * @date 2021/1/30 22:16
 */
public class UserContext {

    // 保存到当前线程的本地变量里面
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
