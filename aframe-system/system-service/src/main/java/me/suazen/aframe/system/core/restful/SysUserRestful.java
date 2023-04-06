package me.suazen.aframe.system.core.restful;

import me.suazen.aframe.framework.core.restful.annotation.Get;
import me.suazen.aframe.framework.core.restful.annotation.Restful;

@Restful("/sysUser")
public interface SysUserRestful {
    @Get(sql="select * from sys_user where login_name = #{loginName} or username = #{username}")
    void querySysUser();
}
