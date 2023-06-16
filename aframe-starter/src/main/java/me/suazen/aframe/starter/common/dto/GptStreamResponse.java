package me.suazen.aframe.starter.common.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GptStreamResponse {
    private String id;

    private String object;

    private String created;

    private String model;

    private String usage;

    private List<Choice> choices;

    @Getter
    @Setter
    public static class Choice{
        private int index;

        private String finish_reason;

        private JSONObject delta;
    }
}
