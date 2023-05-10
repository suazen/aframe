package me.suazen.aframe.framework.web.restful.exception;

public class RestNotFoundException extends RuntimeException{

    /**
     * 
     */
    public RestNotFoundException() {
    }

    /**
     * @param message
     */
    public RestNotFoundException(String message) {
        super(message);
    }
    
}
