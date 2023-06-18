package me.suazen.aframe.auth.login.wxlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StateDTO {
    private String code;

    private String token;
}
