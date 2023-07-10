package me.suazen.aframe.module.echo.common.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.RedisKey;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.common.dto.ChatRequest;
import me.suazen.aframe.module.echo.common.entity.PromptSetting;
import me.suazen.aframe.module.echo.config.props.AzureOpenaiProperties;
import me.suazen.aframe.web.sse.SseClient;
import okhttp3.sse.EventSourceListener;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();

    private static final String search_fun_prompt = "If user ask you about the real-time information, you should reply a json start with “$f” format like $f{“action”:”BingSearch”,”param”:{”query”:{ the key word }}}.";

    public static void callStream(ChatRequest request, EventSourceListener listener){
        callStream(true,request,listener);
    }

    public static void callStream(boolean systemPrompt,ChatRequest request, EventSourceListener listener){
        if (systemPrompt){
            promptSetting(request);
        }
        SseClient.create(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(request.toString().replaceAll("(\n)+","  ").trim())
                .execute(listener);
    }

    private static void promptSetting(ChatRequest request){
        RList<ChatMessage> promptSettings = SpringUtil.getBean(RedissonClient.class).getList(RedisKey.openai_prompt_setting.name());
        if (promptSettings.isEmpty()){
            List<PromptSetting> promptSettingList = new PromptSetting()
                    .state().eq(GlobalConstant.YES)
                    .sort().orderByAsc()
                    .list();
            promptSettings.addAll(promptSettingList.stream()
                    .map(prompt-> ChatMessage.systemPrompt(prompt.getPrompt()))
                    .collect(Collectors.toList()));
        }
        request.getMessages().addAll(0,promptSettings.readAll()
                .stream()
                .peek(prompt-> prompt.setContent(prompt.getContent().replaceAll("\\$\\{time}",DateUtil.now() +" "+ DateUtil.thisDayOfWeekEnum().name())))
                .collect(Collectors.toList()));
        request.getMessages().add(ChatMessage.systemPrompt(search_fun_prompt));
    }
}
