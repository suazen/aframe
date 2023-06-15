package me.suazen.aframe.starter.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.web.sse.SseClient;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.starter.common.dto.ChatRequest;
import me.suazen.aframe.starter.common.dto.GptStreamResponse;
import me.suazen.aframe.starter.common.dto.MessageDTO;
import me.suazen.aframe.starter.common.props.AzureOpenaiProperties;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class AzureOpenaiUtil {
    private static final AzureOpenaiProperties properties = SpringUtil.getBean(AzureOpenaiProperties.class);

    private static final String url = "https://"+properties.getEndPoint()+".openai.azure.com/openai/deployments/"+properties.getModel()+"/chat/completions?api-version="+properties.getVersion();

    private static final String ROLE_USER = "user";

    private static final String ROLE_ASSISTANT = "assistant";

    public static void callStream(String content) {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("text/event-stream;charset=UTF-8");
        ChatRequest chatRequest = new ChatRequest()
                .addMessage(new MessageDTO(ROLE_USER,content));
        SseClient.build(url)
                .method("POST")
                .header("api-key",properties.getApiKey())
                .body(chatRequest.toString())
                .execute()
                .readStream(text->{
                    log.info(text);
                    try {
                    if (StrUtil.isEmpty(text)){
                        response.getOutputStream().write("\n".getBytes());
                        response.getOutputStream().flush();
                        return;
                    }
                    String[] splits = text.split(":");
                        if ("[DONE]".equals(splits[1].trim())){
                            response.getOutputStream().write("data:[DONE]".getBytes());
                            response.getOutputStream().flush();
                        }else {
                            GptStreamResponse streamRes = JSON.parseObject(text.substring(5), GptStreamResponse.class);
                            Map<String,String> delta = streamRes.getChoices().stream().findFirst().orElse(new GptStreamResponse.Choice()).getDelta();
                            if (delta != null && delta.containsKey("content")){
                                response.getOutputStream().write(("data:"+delta.get("content")).getBytes());
                                response.getOutputStream().flush();
                            }
                        }
                    } catch (IOException e) {
                    }
                });
    }
}
