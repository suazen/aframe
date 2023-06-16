package me.suazen.aframe.starter.openai.service;

import me.suazen.aframe.starter.openai.dto.ChatDTO;

public interface OpenaiService {
    void clearChat(String uuid);

    void sendMessage(ChatDTO dto);

    void reGenerate(String uuid,int index);
}
