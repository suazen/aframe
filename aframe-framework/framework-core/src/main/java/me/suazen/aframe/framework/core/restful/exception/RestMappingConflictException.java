package me.suazen.aframe.framework.core.restful.exception;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
public class RestMappingConflictException extends RuntimeException{
    public RestMappingConflictException() {
        super();
    }

    public RestMappingConflictException(String message) {
        super(message);
    }

    public RestMappingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
