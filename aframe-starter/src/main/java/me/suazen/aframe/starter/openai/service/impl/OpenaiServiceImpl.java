package me.suazen.aframe.starter.openai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.starter.common.dto.ChatRequest;
import me.suazen.aframe.starter.common.dto.ChatMessage;
import me.suazen.aframe.starter.common.util.AzureOpenaiUtil;
import me.suazen.aframe.starter.openai.handler.OpenaiStreamHandler;
import me.suazen.aframe.starter.openai.service.OpenaiService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class OpenaiServiceImpl implements OpenaiService {
    public static final String SESSION_KEY = "openai_chat_his:";

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void clearChat() {
        getChatMessages().clear();
    }

    @Override
    public void sendMessage(String message) {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("text/event-stream;charset=UTF-8");
        OpenaiStreamHandler streamHandler = new OpenaiStreamHandler(response);
        RList<ChatMessage> messages = getChatMessages();
        messages.add(ChatMessage.userMsg(message.trim()));
        AzureOpenaiUtil.callStream(new ChatRequest().setMessages(messages),streamHandler);
        messages.add(ChatMessage.botMsg(streamHandler.getContent()));
    }


    private RList<ChatMessage> getChatMessages(){
        String token = StpUtil.getTokenValue();
        return redissonClient.getList(SESSION_KEY+token);
    }
}
