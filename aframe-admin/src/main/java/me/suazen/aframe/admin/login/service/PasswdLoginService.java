package me.suazen.aframe.admin.login.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.exception.UserPasswordNotMatchException;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.admin.login.dto.PasswdLoginBody;
import me.suazen.aframe.system.core.entity.SysUser;
import me.suazen.aframe.system.core.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Service("PASSWD_LOGIN_HANDLER")
public class PasswdLoginService extends BaseLoginService<SysUser> {

    public PasswdLoginService(){
        this.stpLogic = StpUtil.getStpLogic();
    }

    @Override
    protected SysUser doCheck(JSONObject loginBody) {
        PasswdLoginBody body = loginBody.to(PasswdLoginBody.class);
        SysUser sysUser = new SysUser()
                .username().eq(body.getUsername())
                .password().eq(encryptPassword(body.getPassword()))
                .one();
        if (sysUser == null){
            throw new UserPasswordNotMatchException();
        }

        return sysUser;
    }

    private String encryptPassword(String originPwd){
        return SmUtil.sm3(originPwd);
    }

}
