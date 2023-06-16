package me.suazen.aframe.starter.common.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.web.sse.SseClient;
import me.suazen.aframe.framework.web.sse.handler.StreamEventHandler;
import me.suazen.aframe.starter.common.dto.ChatRequest;
import me.suazen.aframe.starter.common.props.AzureOpenaiProperties;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();

    public static final String SESSION_KEY = "openai_chat_his:";

    public static void callStream(ChatRequest request, StreamEventHandler eventHandler) {
        SseClient.build(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(request.toString().trim())
                .execute()
                .readStream(eventHandler);
    }
}
