package com.tcmyxc.result;

/**
 * @author tcmyxc
 * @date 2021/1/10 22:14
 */
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if(cm == null){
            return ;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    // 成功的时候调用
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    // 失败的时候调用
    public static <T> Result<T> error(CodeMsg cm){
        return new Result<T>(cm);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

}
