package com.tcmyxc.util;

import java.util.UUID;

/**
 * @author 徐文祥
 * @date 2021/1/15 1:37
 */
public class UUIDUtil {
    public static String uuid(){
        // 随机生成一个 uuid，并且去掉中间的横杠
        return UUID.randomUUID().toString().replace("-", "");
    }
}
