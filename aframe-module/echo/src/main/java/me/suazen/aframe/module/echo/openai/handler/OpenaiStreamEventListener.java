package me.suazen.aframe.module.echo.openai.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BaseException;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.common.dto.GptStreamResponse;
import me.suazen.aframe.web.sse.listener.AbstractEventSourceListener;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RAtomicLong;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author sujizhen
 * @date 2023-07-03
 **/
@Slf4j
public class OpenaiStreamEventListener extends AbstractEventSourceListener {
    private final SseEmitter sseEmitter;

    private final RAtomicLong times;

    private StringBuilder contentBuilder;


    public OpenaiStreamEventListener(SseEmitter sseEmitter,RAtomicLong times){
        this.sseEmitter = sseEmitter;
        this.times = times;
        contentBuilder = new StringBuilder();
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        this.sseEmitter.complete();
        this.times.decrementAndGet();
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        try {
            GptStreamResponse streamRes = JSON.parseObject(data, GptStreamResponse.class);
            JSONObject delta = streamRes.getChoices().stream().findFirst().orElse(new GptStreamResponse.Choice()).getDelta();
            if (delta != null && delta.containsKey("content")) {
                contentBuilder.append(delta.getString("content"));
                sseEmitter.send(delta.toString());
            }
        } catch (IOException e) {
            log.error("输出流失败");
            this.contentBuilder = null;
            throw new BaseException("Openai流输出失败："+e.getMessage(),e);
        }
    }

    @Override
    public void afterFailure(String msg) {
        this.sseEmitter.completeWithError(new BusinessException(msg));
    }

    public String getContent(){
        return this.contentBuilder.toString();
    }

}
