package com.tcmyxc.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author 徐文祥
 * @date 2021/1/13 22:00
 */
public class MD5Util {

    // 来把盐
    private static final String salt = "1a2b3c4d";

    // 对明文字符串进行 MD5 加密
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    // 把用户的明文加点盐
    public static String inputPassToFormPass(String inputPass) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        //System.out.println(str);
        return md5(str);
    }

    // 对客户端加密过的密码再加密一次放到数据库
    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    // 把两次加密过程封装起来
    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
//        System.out.println(inputPassToFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
//		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
		System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
    }
}
