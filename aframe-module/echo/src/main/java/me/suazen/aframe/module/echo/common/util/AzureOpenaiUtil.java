package me.suazen.aframe.module.echo.common.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.props.AzureOpenaiProperties;
import me.suazen.aframe.framework.core.util.DateUtil;
import me.suazen.aframe.framework.web.sse.SseClient;
import me.suazen.aframe.framework.web.sse.handler.StreamEventHandler;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;

import java.util.Arrays;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();

    private static final String ai_name_prompt = "Your name is 小E.";

//    private static final String role_prompt = "I want you to act as a product manager. You will act as the product manager for a company that provides products for its customers. Your job is to develop products for their specific needs and goals. You should act as an interface designer, product manager, and developer. " +
//            "You will only answer professional questions relevant to your role. Our conversation will be under Chinese.";
    private static final String role_prompt = "I want you to act as a help assistant. I will provide you with a list of questions and you will answer them. Our conversation will be under Chinese.";

    public static void callStream(ChatRequest request, StreamEventHandler eventHandler) {
        request.getMessages().addAll(0,Arrays.asList(
                ChatMessage.systemPrompt(ai_name_prompt)
                ,ChatMessage.systemPrompt("Today is %s %s",DateUtil.today(),DateUtil.thisDayOfWeekEnum().name())
                ,ChatMessage.systemPrompt(role_prompt)
        ));
        SseClient.build(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(request.toString().replaceAll("(\n)+","  ").trim())
                .execute(eventHandler);
    }
}
