package me.suazen.aframe.framework.core.exception;

/**
 * @author sujizhen
 * @date 2023-06-13
 **/
public class ResourceException extends BaseException{
    public ResourceException() {
        super();
    }

    public ResourceException(String message, String... args) {
        super(message, args);
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
