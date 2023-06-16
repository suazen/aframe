package me.suazen.aframe.starter.openai.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.exception.BaseException;
import me.suazen.aframe.framework.web.sse.handler.StreamEventHandler;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.starter.common.dto.GptStreamResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sujizhen
 * @date 2023-06-16
 **/
@Slf4j
public class OpenaiStreamHandler implements StreamEventHandler {
    private final HttpServletResponse response;

    private StringBuilder contentBuilder;

    public OpenaiStreamHandler(HttpServletResponse response){
        this.response = response;
        contentBuilder = new StringBuilder();
    }

    @Override
    public void readStream(String text) {
        try {
            if (StrUtil.isEmpty(text)) {
                response.getOutputStream().write("\n".getBytes());
                response.getOutputStream().flush();
                return;
            }
            if (!text.startsWith("data:")){
                return;
            }
            String content = text.substring("data:".length());
            if ("[DONE]".equals(content.trim())) {
                response.getOutputStream().write("[DONE]".getBytes());
                response.getOutputStream().flush();
            } else {
                GptStreamResponse streamRes = JSON.parseObject(content, GptStreamResponse.class);
                JSONObject delta = streamRes.getChoices().stream().findFirst().orElse(new GptStreamResponse.Choice()).getDelta();
                if (delta != null && delta.containsKey("content")) {
                    contentBuilder.append(delta.getString("content"));
                    response.getOutputStream().write(delta.toString().getBytes());
                    response.getOutputStream().flush();
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
        response.setStatus(500);
        ServletUtil.write(response,json,"application/json;charset=UTF-8");
    }

    @Override
    public void onComplete() {
        try {
            response.getOutputStream().close();
        }catch (IOException e){
            log.error("Response输出流关闭失败");
        }
    }

    public String getContent(){
        return this.contentBuilder.toString();
    }
}
