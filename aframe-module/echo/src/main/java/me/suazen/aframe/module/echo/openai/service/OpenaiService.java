package me.suazen.aframe.module.echo.openai.service;

import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface OpenaiService {
    SseEmitter sendMessage(ChatDTO dto);

    SseEmitter reGenerate(String uuid,int index);

    List<ChatMessage> queryHisMessages(String uuid);
}
