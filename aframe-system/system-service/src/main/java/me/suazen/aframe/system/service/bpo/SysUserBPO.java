package me.suazen.aframe.system.service.bpo;

import me.suazen.aframe.framework.core.restful.annotation.Get;
import me.suazen.aframe.framework.core.restful.annotation.Post;
import me.suazen.aframe.framework.core.restful.annotation.Restful;
import me.suazen.aframe.system.core.entity.SysUser;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
@Restful("/sysUser")
public interface SysUserBPO {
    @Get("/register")
    void Register();

    @Get("/rpc")
    SysUser rpc(String username);

    @Post(path = "/queryByUsername")
    SysUser queryByUsername(String username,String loginName);
}
