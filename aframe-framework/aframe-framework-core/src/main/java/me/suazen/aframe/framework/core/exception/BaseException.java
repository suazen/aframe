package me.suazen.aframe.framework.core.exception;

/**
 * @author sujizhen
 * @date 2023-05-10
 **/
public class BaseException extends RuntimeException{
    public BaseException() {
        super();
    }

    public BaseException(String message,String... args) {
        super(String.format(message,args));
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
