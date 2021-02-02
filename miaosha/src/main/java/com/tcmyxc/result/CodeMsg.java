package com.tcmyxc.result;

/**
 * @author tcmyxc
 * @date 2021/1/10 22:27
 */
public class CodeMsg {

    private int code;
    private String msg;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_EXCEPTION = new CodeMsg(500101, "参数检验异常: %s");
    public static final CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求不合法");
    // 登录模块 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "密码为空，请检查输入");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号错误");
    public static CodeMsg MOBILE_NOT_EXITS = new CodeMsg(500214, "手机号未注册");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误，请检查");

    // 商品模块 5003xx

    // 订单模块 5004xx
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");

    // 秒杀模块 5005xx
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500, "商品已经秒杀结束！");
    public static CodeMsg REPEATE_MIAOSHA_ORDER = new CodeMsg(500501, "一个用户只能秒杀一件商品！");
    public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败！");
    public static final CodeMsg VERIFY_CODE_ERROR = new CodeMsg(500503, "验证码错误");
    public static final CodeMsg ACCESS_LIMITED = new CodeMsg(500504, "访问次数超过限制");


    public CodeMsg() {
    }

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
