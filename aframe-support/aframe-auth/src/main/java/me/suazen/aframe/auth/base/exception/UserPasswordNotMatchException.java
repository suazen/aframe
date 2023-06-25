package me.suazen.aframe.auth.base.exception;

import me.suazen.aframe.core.exception.BaseException;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
public class UserPasswordNotMatchException extends BaseException {
    public UserPasswordNotMatchException() {
        super("用户不存在/密码错误");
    }
}
