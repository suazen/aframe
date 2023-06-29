package me.suazen.aframe.module.echo.common.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.Constant;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;
import me.suazen.aframe.module.echo.common.entity.PromptSetting;
import me.suazen.aframe.module.echo.config.props.AzureOpenaiProperties;
import me.suazen.aframe.web.sse.SseClient;
import me.suazen.aframe.web.sse.handler.StreamEventHandler;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();

//    private static final String role_prompt = "I want you to act as a product manager. You will act as the product manager for a company that provides products for its customers. Your job is to develop products for their specific needs and goals. You should act as an interface designer, product manager, and developer. " +
//            "You will only answer professional questions relevant to your role. Our conversation will be under Chinese.";
    private static final String role_prompt = "I want you to act as a help assistant. I will provide you with a list of questions and you will answer them. Our conversation will be under Chinese.";

    public static void callStream(ChatRequest request, StreamEventHandler eventHandler) {
        promptSetting(request);
        SseClient.build(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(request.toString().replaceAll("(\n)+","  ").trim())
                .execute(eventHandler);
    }

    private static void promptSetting(ChatRequest request){
        RList<ChatMessage> promptSettings = SpringUtil.getBean(RedissonClient.class).getList(Constant.REDIS_KEY_PROMPT_SETTING);
        if (promptSettings.isEmpty()){
            List<PromptSetting> promptSettingList = new PromptSetting()
                    .state().eq(GlobalConstant.YES)
                    .sort().orderByAsc()
                    .list();
            promptSettings.addAll(promptSettingList.stream()
                    .map(prompt-> ChatMessage.systemPrompt(prompt.getPrompt()))
                    .collect(Collectors.toList()));
        }
        request.getMessages().addAll(0,promptSettings.readAll());
        request.getMessages().add(0,ChatMessage.systemPrompt("Today is %s %s",DateUtil.today(),DateUtil.thisDayOfWeekEnum().name()));
    }
}
