package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/13 19:54
 */
public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
