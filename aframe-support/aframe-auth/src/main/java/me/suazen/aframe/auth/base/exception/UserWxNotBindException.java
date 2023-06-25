package me.suazen.aframe.auth.base.exception;

import me.suazen.aframe.core.exception.BusinessException;

/**
 * @author sujizhen
 * @date 2023-06-12
 **/
public class UserWxNotBindException extends BusinessException {
    public UserWxNotBindException() {
        super("该微信尚未绑定任何账号");
    }
}
