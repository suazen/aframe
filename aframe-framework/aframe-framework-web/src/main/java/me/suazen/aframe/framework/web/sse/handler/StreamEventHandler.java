package me.suazen.aframe.framework.web.sse.handler;

public interface StreamEventHandler {
    void readStream(String line);

    void onComplete();
}
