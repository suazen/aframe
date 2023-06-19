package me.suazen.aframe.starter.openai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.exception.BusinessException;
import me.suazen.aframe.framework.core.manager.AsyncManager;
import me.suazen.aframe.framework.core.util.DateUtil;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.starter.common.dto.ChatMessage;
import me.suazen.aframe.starter.common.dto.ChatRequest;
import me.suazen.aframe.starter.common.entity.ChatHis;
import me.suazen.aframe.starter.common.mapper.ChatHisMapper;
import me.suazen.aframe.starter.common.util.AzureOpenaiUtil;
import me.suazen.aframe.starter.openai.dto.ChatDTO;
import me.suazen.aframe.starter.openai.handler.OpenaiStreamHandler;
import me.suazen.aframe.starter.openai.service.OpenaiService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.TimerTask;

@Service
@Slf4j
public class OpenaiServiceImpl implements OpenaiService {
    public static final String SESSION_KEY = "openai_chat_his:";

    @Resource
    private RedissonClient redissonClient;

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
            AsyncManager.me().execute(new SaveChatHistoryTasker((String) StpUtil.getLoginId(),dto.getUuid(),"user",dto.getContent().trim(),messages.size()-1));
        }
        //调用openai接口
        AzureOpenaiUtil.callStream(new ChatRequest().setMessages(messages.readAll()),streamHandler);
        //保存接口返回的完整内容
        messages.add(ChatMessage.botMsg(streamHandler.getContent()));
        AsyncManager.me().execute(new SaveChatHistoryTasker((String) StpUtil.getLoginId(),dto.getUuid(),"assistant",streamHandler.getContent(),messages.size()-1));
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
        RList<ChatMessage> redisList = redissonClient.getList(SESSION_KEY+uuid);
        redisList.expire(Duration.ofDays(1));
        if (redisList.isEmpty()){
            List<ChatHis> chatHisList = new ChatHis()
                    .conversationId().eq(uuid)
                    .chatIndex().orderByAsc()
                    .list();
            chatHisList.forEach(chatHis -> redisList.add(new ChatMessage(chatHis.getRole(),chatHis.getContent())));
        }
        return redisList;
    }

    @AllArgsConstructor
    private static class SaveChatHistoryTasker extends TimerTask{
        private final String userId;
        private final String conversationId;
        private final String role;
        private final String content;
        private final int index;

        @Override
        public void run() {
            ChatHis chatHis = new ChatHis();
            chatHis.setUserId(userId);
            chatHis.setChatTime(DateUtil.nowSimple());
            chatHis.setRole(role);
            chatHis.setContent(content);
            chatHis.setConversationId(conversationId);
            chatHis.setChatIndex(index);
            SpringUtil.getBean(ChatHisMapper.class).insert(chatHis);
        }
    }
}
