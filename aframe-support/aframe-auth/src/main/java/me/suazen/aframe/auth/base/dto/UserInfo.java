package me.suazen.aframe.auth.base.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.suazen.aframe.core.base.pojo.User;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class UserInfo<T extends User> implements Serializable {

    private T user;

    private List<String> roles;

    private List<String> permissions;
}
