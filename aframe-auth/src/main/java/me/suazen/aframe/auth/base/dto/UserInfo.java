package me.suazen.aframe.auth.base.dto;

import cn.dev33.satoken.stp.StpUtil;
import lombok.Getter;
import lombok.Setter;
import me.suazen.aframe.system.core.entity.SysUser;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserInfo implements Serializable {

    private SysUser user;

    private List<String> roles;

    private List<String> permissions;

    public static UserInfo getBySysUser(SysUser sysUser){
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(sysUser);
        userInfo.setRoles(StpUtil.getRoleList());
        userInfo.setPermissions(StpUtil.getPermissionList());
        return userInfo;
    }
}
