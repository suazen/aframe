package me.suazen.aframe.framework.web.sse.exception;

import me.suazen.aframe.framework.core.exception.BaseException;

public class SseClientException extends BaseException {
    public SseClientException(String message, String... args) {
        super(message, args);
    }

    public SseClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
