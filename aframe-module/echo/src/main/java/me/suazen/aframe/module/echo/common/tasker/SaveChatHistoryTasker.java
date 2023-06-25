package me.suazen.aframe.module.echo.common.tasker;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.entity.ChatHis;
import me.suazen.aframe.module.echo.common.mapper.ChatHisMapper;

import java.util.TimerTask;

/**
 * @author sujizhen
 * @date 2023-06-21
 **/
@AllArgsConstructor

public class SaveChatHistoryTasker extends TimerTask {
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