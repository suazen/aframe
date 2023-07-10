package me.suazen.aframe.module.echo.openai.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BaseException;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;
import me.suazen.aframe.module.echo.common.dto.GptStreamResponse;
import me.suazen.aframe.module.echo.common.util.AzureOpenaiUtil;
import me.suazen.aframe.module.echo.plugins.base.IPlugin;
import me.suazen.aframe.module.echo.plugins.exception.BasePluginException;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * @author sujizhen
 * @date 2023-07-03
 **/
@Slf4j
public abstract class OpenaiStreamEventListener extends EventSourceListener {
    private final SseEmitter sseEmitter;

    private StringBuilder contentBuilder;

    private int index = 0;

    private boolean functionCall = false;


    public OpenaiStreamEventListener(SseEmitter sseEmitter){
        this.sseEmitter = sseEmitter;
        contentBuilder = new StringBuilder();
    }

    private void initListener(){
        this.contentBuilder = new StringBuilder();
        this.index = 0;
        this.functionCall = false;
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.info("Azure接口连接成功");
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        if (functionCall){
            log.info("调用插件==》{}",this.contentBuilder);
            JSONObject funcInfo = JSON.parseObject(this.contentBuilder.substring(2));
            initListener();
            String funName = funcInfo.getString("action");
            IPlugin plugin = SpringUtil.getBean(funName+"_PLUGIN", IPlugin.class);
            try {
                JSONObject meta = new JSONObject();
                meta.put("metaInfo",plugin.metaInfo(funcInfo.getString("param")));
                this.sseEmitter.send(meta.toString());
            } catch (IOException e) {
                log.error("输出流失败",e);
                this.contentBuilder = null;
                this.sseEmitter.completeWithError(new BasePluginException("插件调用失败了T^T"));
                return;
            }
            this.onFunctionCall(plugin.run(funcInfo.getString("param")));
        }else {
            this.sseEmitter.complete();
            this.onComplete();
        }
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        try {
            GptStreamResponse streamRes = JSON.parseObject(data, GptStreamResponse.class);
            JSONObject delta = streamRes.getChoices().stream().findFirst().orElse(new GptStreamResponse.Choice()).getDelta();
            if (delta != null && delta.containsKey("content")) {
                if (index == 0 && "$f".equals(delta.getString("content"))){
                    functionCall = true;
                }else if (!functionCall){
                    sseEmitter.send(SseEmitter.event().data(delta.toString()));
                }
                contentBuilder.append(delta.getString("content"));
                index++;
            }
        } catch (IOException e) {
            log.error("输出流失败",e);
            this.contentBuilder = null;
            this.sseEmitter.completeWithError(new BaseException("机器人开小差了，请重试一下吧~"));
        }
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        String msg = "机器人开小差了，请重试一下吧~";
        if (t != null) {
            log.error("调用Azure接口失败", t);
        }else if (response != null) {
            try {
                if (response.body() != null) {
                    String bodyString = response.body().string();
                    log.error("调用Azure接口失败，返回body：{}",bodyString);
                    JSONObject bodyJson = JSON.parseObject(bodyString);
                    if (bodyJson.containsKey("error")){
                        JSONObject error = bodyJson.getJSONObject("error");
                        if ("context_length_exceeded".equals(error.getString("code"))){
                            msg = "抱歉，本轮对话已达到最大限定字数😭 要继续使用请点击左下方发起新的对话👇";
                        }else if ("content_filter".equals(error.getString("code"))){
                            msg = "您的问题中存在限制内容🙈，请换个话题吧~";
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取response.body失败", e);
            }
        }
        this.sseEmitter.completeWithError(new BusinessException(msg));
        eventSource.cancel();
    }

    public String getContent(){
        return this.contentBuilder.toString();
    }

    public abstract void onComplete();

    public void onFunctionCall(List<ChatMessage> messages){
        AzureOpenaiUtil.callStream(false,new ChatRequest().setTemperature(0).setMessages(messages),this);
    };
}
