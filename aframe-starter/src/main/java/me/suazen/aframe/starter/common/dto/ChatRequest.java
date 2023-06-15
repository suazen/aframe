package me.suazen.aframe.starter.common.dto;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ChatRequest implements Serializable {
    private List<MessageDTO> messages;

    private boolean stream;

    public ChatRequest(){
        this.messages = new ArrayList<>();
        this.stream = true;
    }

    public ChatRequest(boolean stream){
        this.messages = new ArrayList<>();
        this.stream = stream;
    }

    public ChatRequest addMessage(MessageDTO message){
        if (messages == null){
            messages = new ArrayList<>();
        }
        this.messages.add(message);
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
