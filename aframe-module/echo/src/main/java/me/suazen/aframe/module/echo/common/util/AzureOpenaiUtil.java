package me.suazen.aframe.module.echo.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.knuddels.jtokkit.api.Encoding;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.PromptPos;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();


    public static void callStream(AtomicInteger tokens, ChatRequest request, EventSourceListener listener){
        callStream(true,tokens,request,listener);
    }

    public static void callStream(boolean systemPrompt,AtomicInteger tokens,ChatRequest request, EventSourceListener listener){
        if (systemPrompt){
            promptSetting(request);
        }
        for (ChatMessage message : request.getMessages()){
            tokens.addAndGet(SpringUtil.getBean(Encoding.class).countTokens(message.getContent()));
        }
        SseClient.create(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(request.toString().replaceAll("(\n)+","  ").trim())
                .execute(listener);
    }

    private static void promptSetting(ChatRequest request){
        RList<PromptSetting> promptSettings = SpringUtil.getBean(RedissonClient.class).getList(RedisKey.openai_prompt_setting.name());
        if (promptSettings.isEmpty()){
            List<PromptSetting> promptSettingList = new PromptSetting()
                    .state().eq(GlobalConstant.YES)
                    .pos().orderByAsc()
                    .sort().orderByAsc()
                    .list();
            promptSettings.addAll(promptSettingList);
        }
        request.getMessages().addAll(0,promptSettings.readAll()
                .stream()
                .filter(prompt -> PromptPos.TOP.name().equals(prompt.getPos()))
                .map(prompt-> ChatMessage.systemPrompt(prompt.getPrompt().replaceAll("\\$\\{time}",DateUtil.now() +" "+ DateUtil.thisDayOfWeekEnum().name())))
                .collect(Collectors.toList()));
        request.getMessages().addAll(promptSettings.readAll()
                .stream()
                .filter(prompt->PromptPos.END.name().equals(prompt.getPos()))
                .map(prompt-> ChatMessage.systemPrompt(prompt.getPrompt().replaceAll("\\$\\{time}",DateUtil.now() +" "+ DateUtil.thisDayOfWeekEnum().name())))
                .collect(Collectors.toList()));
    }
}
