package com.tcmyxc.exception;

import com.tcmyxc.result.CodeMsg;

/**
 * @author 徐文祥
 * @date 2021/1/15 1:02
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getcodeMsg() {
        return codeMsg;
    }
}
