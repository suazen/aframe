package me.suazen.aframe.module.echo.openai.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BaseException;
import me.suazen.aframe.module.echo.common.dto.GptStreamResponse;
import me.suazen.aframe.web.sse.handler.StreamEventHandler;
import me.suazen.aframe.web.util.ServletUtil;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author sujizhen
 * @date 2023-06-16
 **/
@Slf4j
public class OpenaiStreamHandler implements StreamEventHandler {
    private final SseEmitter sseEmitter;

    private StringBuilder contentBuilder;

    public OpenaiStreamHandler(SseEmitter sseEmitter){
        this.sseEmitter = sseEmitter;
        contentBuilder = new StringBuilder();
    }

    @Override
    public void readStream(String text) {
        try {
            if (!text.startsWith("data:")){
                return;
            }
            String content = text.substring("data:".length());
            if ("[DONE]".equals(content.trim())) {
                sseEmitter.send("[DONE]");
            } else {
                GptStreamResponse streamRes = JSON.parseObject(content, GptStreamResponse.class);
                JSONObject delta = streamRes.getChoices().stream().findFirst().orElse(new GptStreamResponse.Choice()).getDelta();
                if (delta != null && delta.containsKey("content")) {
                    contentBuilder.append(delta.getString("content"));
                    sseEmitter.send(delta.toString());
                }
            }
        } catch (IOException e) {
            log.error("输出流失败");
            this.contentBuilder = null;
            throw new BaseException("Openai流输出失败："+e.getMessage(),e);
        }
    }

    @Override
    public void writeError(String json) {
        contentBuilder = new StringBuilder(json);
        ServletUtil.write(ServletUtil.getResponse(),json,"application/json;charset=UTF-8");
    }

    @Override
    public void onComplete() {
        sseEmitter.complete();
    }

    public String getContent(){
        return this.contentBuilder.toString();
    }
}
