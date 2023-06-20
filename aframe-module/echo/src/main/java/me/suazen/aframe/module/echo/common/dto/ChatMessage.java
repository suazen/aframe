package me.suazen.aframe.module.echo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class ChatMessage implements Serializable {
    private String role;

    private String content;

    public static ChatMessage userMsg(String content){
        return new ChatMessage("user",content);
    }

    public static ChatMessage botMsg(String content){
        return new ChatMessage("assistant",content);
    }

    public static ChatMessage systemPrompt(String content,Object... value){
        return new ChatMessage("system",String.format(content,value));
    }
}
