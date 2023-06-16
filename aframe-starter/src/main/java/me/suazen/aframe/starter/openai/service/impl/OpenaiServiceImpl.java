package me.suazen.aframe.starter.openai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.exception.BusinessException;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.starter.common.dto.ChatRequest;
import me.suazen.aframe.starter.common.dto.ChatMessage;
import me.suazen.aframe.starter.common.util.AzureOpenaiUtil;
import me.suazen.aframe.starter.openai.dto.ChatDTO;
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
    public void clearChat(String uuid) {
        if (StrUtil.isEmpty(uuid)){
            return;
        }
        getChatMessages(uuid).clear();
    }

    @Override
    public void sendMessage(ChatDTO dto) {
        //获取HttpServletResponse，设置请求头并传入流解析器
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("text/event-stream;charset=UTF-8");
        OpenaiStreamHandler streamHandler = new OpenaiStreamHandler(response);
        //从redis获取聊天记录
        RList<ChatMessage> messages = getChatMessages(dto.getUuid());
        //如果有content则添加，没有表示重新回答
        if (StrUtil.isNotBlank(dto.getContent())) {
            messages.add(ChatMessage.userMsg(dto.getContent().trim()));
        }
        //调用openai接口
        AzureOpenaiUtil.callStream(new ChatRequest().setMessages(messages),streamHandler);
        //保存接口返回的完整内容
        messages.add(ChatMessage.botMsg(streamHandler.getContent()));
    }

    @Override
    public void reGenerate(String uuid, int index) {
        RList<ChatMessage> messages = getChatMessages(uuid);
        if (index > messages.size()){
            throw new BusinessException("系统异常，无法重新生成回答");
        }
        messages.removeAll(messages.subList(index,messages.size()));
        sendMessage(new ChatDTO(uuid,null));
    }

    private RList<ChatMessage> getChatMessages(String uuid){
        return redissonClient.getList(SESSION_KEY+uuid);
    }
}
