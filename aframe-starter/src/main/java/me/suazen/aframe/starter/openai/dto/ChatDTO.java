package me.suazen.aframe.starter.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    @NotBlank(message = "uuid不能为空")
    private String uuid;

    @NotBlank(message = "发送内容不能为空")
    private String content;
}
