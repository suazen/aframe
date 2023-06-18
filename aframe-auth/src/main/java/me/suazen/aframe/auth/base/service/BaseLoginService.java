package me.suazen.aframe.auth.base.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.dto.UserInfo;
import me.suazen.aframe.framework.core.util.DateUtil;
import me.suazen.aframe.framework.web.util.IpUtils;
import me.suazen.aframe.framework.web.util.ServletUtil;
import me.suazen.aframe.system.core.entity.SysUser;
import me.suazen.aframe.system.core.mapper.SysUserMapper;

import javax.annotation.Resource;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
public abstract class BaseLoginService {
    @Resource
    protected SysUserMapper sysUserMapper;

    public String login(JSONObject loginBody){
        SysUser user = doCheck(loginBody);
        StpUtil.login(user.getUserId());
        doAfterLogin(loginBody,user);
        return StpUtil.getTokenValue();
    };

    protected abstract SysUser doCheck(JSONObject loginBody);

    protected void doAfterLogin(JSONObject body,SysUser user){
        saveUserSession(user);
    }

    private void saveUserSession(SysUser user){
        user.setLoginDate(DateUtil.nowSimple());
        user.setLoginIp(IpUtils.getIpAddr(ServletUtil.getRequest()));
        sysUserMapper.updateById(user);

        StpUtil.getSession().set(SaSession.USER, UserInfo.getBySysUser(user));
    }
}
