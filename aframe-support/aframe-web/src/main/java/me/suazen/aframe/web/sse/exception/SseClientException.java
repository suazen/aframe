package me.suazen.aframe.web.sse.exception;

import me.suazen.aframe.core.exception.BaseException;

public class SseClientException extends BaseException {
    public SseClientException(String message, String... args) {
        super(message, args);
    }

    public SseClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
