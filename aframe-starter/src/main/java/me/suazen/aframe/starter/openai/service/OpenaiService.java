package me.suazen.aframe.starter.openai.service;

public interface OpenaiService {
    void clearChat();

    void sendMessage(String message);

}
