package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/13 19:49
 */
public interface KeyPrefix {

    int expireSeconds();
    String getPrefix();
}
