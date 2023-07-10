package me.suazen.aframe.module.echo.plugins.exception;

import me.suazen.aframe.core.exception.BaseException;

/**
 * @author sujizhen
 * @date 2023-07-10
 **/
public class BasePluginException extends BaseException {
    public BasePluginException(String message, String... args) {
        super(message, args);
    }

    public BasePluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
