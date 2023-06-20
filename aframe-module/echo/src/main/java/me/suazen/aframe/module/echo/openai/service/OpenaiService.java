package me.suazen.aframe.module.echo.openai.service;

import me.suazen.aframe.module.echo.openai.dto.ChatDTO;

public interface OpenaiService {
    void sendMessage(ChatDTO dto);

    void reGenerate(String uuid,int index);
}
