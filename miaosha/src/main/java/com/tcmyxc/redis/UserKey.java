package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/13 19:54
 */
public class UserKey extends BasePrefix{

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

    private UserKey(String prefix) {
        super(prefix);
    }
}
