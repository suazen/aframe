package me.suazen.aframe.auth.base.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.suazen.aframe.auth.base.dto.UserInfo;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.system.core.base.User;
import me.suazen.aframe.web.util.IpUtils;
import me.suazen.aframe.web.util.ServletUtil;

import java.util.Optional;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
public abstract class BaseLoginService<T extends User,M extends BaseMapper<T>>{
    public StpLogic stpLogic;

    protected M mapper;

    protected abstract T doCheck(JSONObject loginBody);

    public String login(JSONObject loginBody){
        T user = doCheck(loginBody);
        stpLogic.login(user.getUserId());
        doAfterLogin(loginBody,user);
        return stpLogic.getTokenValue();
    };

    protected void doAfterLogin(JSONObject body, T user){
        user.setLoginDate(DateUtil.nowSimple());
        user.setLoginIp(IpUtils.getIpAddr(ServletUtil.getRequest()));
        mapper.updateById(user);
        stpLogic.getSession().set(SaSession.USER, getUserInfo(user));
    }

    public UserInfo<T> getUserInfo(T user){
        return new UserInfo<T>()
                .setUser(Optional.ofNullable(user).orElse(mapper.selectById((String)stpLogic.getLoginId())))
                .setRoles(stpLogic.getRoleList())
                .setPermissions(stpLogic.getPermissionList());
    }
}
