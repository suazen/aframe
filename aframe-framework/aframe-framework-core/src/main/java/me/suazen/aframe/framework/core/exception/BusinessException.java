package me.suazen.aframe.framework.core.exception;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
public class BusinessException extends BaseException{
    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
