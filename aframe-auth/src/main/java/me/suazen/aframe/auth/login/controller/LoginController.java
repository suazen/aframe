package me.suazen.aframe.auth.login.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.framework.web.domain.AjaxResult;
import me.suazen.aframe.system.core.entity.SysUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@RestController
public class LoginController {

    @PostMapping("/{type}/login")
    public AjaxResult login(@PathVariable("type")String type, @RequestBody @Validated JSONObject loginBody){
        BaseLoginService loginService = SpringUtil.getBean(type.toUpperCase()+"_LOGIN_HANDLER", BaseLoginService.class);
        if (loginService == null){
            return AjaxResult.error("Method Not Found");
        }
        return AjaxResult.success().addData("token",loginService.login(loginBody));
    }

    @GetMapping("/getInfo")
    public AjaxResult getInfo(){
        SaSession session = StpUtil.getSession();
        if (session.get(SaSession.USER) == null){
            String userId = (String) StpUtil.getLoginId();
            SysUser user = new SysUser().userId().eq(userId).one();

            session.set(SaSession.USER, BaseLoginService.getUserInfo(user));
        }
        return AjaxResult.success(session.get(SaSession.USER));
    }

    @PostMapping("/logout")
    public AjaxResult logout(){
        StpUtil.logout();
        return AjaxResult.success();
    }
}
