package me.suazen.aframe.framework.web.sse.handler;

public interface StreamEventHandler {
    void readStream(String line);

    void writeError(String json);

    void onComplete();
}
