package me.suazen.aframe.auth.base.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String userId;

    private String username;

    private String nickname;

    private String avatar;

    private String sex;
}
