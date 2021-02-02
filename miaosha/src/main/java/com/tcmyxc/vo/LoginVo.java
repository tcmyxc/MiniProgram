package com.tcmyxc.vo;

import com.tcmyxc.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author 徐文祥
 * @date 2021/1/13 22:29
 */
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 6)
    private String password;

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "LoginVo {mobile=" + mobile + ", password=" + password + "}";
    }
}
