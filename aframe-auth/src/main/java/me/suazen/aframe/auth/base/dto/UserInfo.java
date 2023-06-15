package me.suazen.aframe.auth.base.dto;

import lombok.Getter;
import lombok.Setter;
import me.suazen.aframe.system.core.entity.SysUser;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserInfo implements Serializable {
//    private String userId;
//
//    private String username;
//
//    private String nickname;
//
//    private String avatar;
//
//    private String sex;
    private SysUser user;

    private List<String> roles;

    private List<String> permissions;
}
