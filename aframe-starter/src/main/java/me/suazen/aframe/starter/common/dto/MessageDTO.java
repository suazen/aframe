package me.suazen.aframe.starter.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class MessageDTO implements Serializable {
    private String role;

    private String content;
}
