package me.suazen.aframe.auth.base.controller;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.web.domain.AjaxResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @PostMapping("/{type}/login")
    public AjaxResult login(@PathVariable("type")String type, @RequestBody @Validated JSONObject loginBody){
        BaseLoginService<?> loginService = SpringUtil.getBean(type.toUpperCase()+"_LOGIN_HANDLER", BaseLoginService.class);
        if (loginService == null){
            return AjaxResult.error("Method Not Found");
        }
        return AjaxResult.success().addData("token",loginService.login(loginBody));
    }

    @GetMapping("/{type}/getInfo")
    public AjaxResult getInfo(@PathVariable("type")String type){
        BaseLoginService<?> loginService = SpringUtil.getBean(type.toUpperCase()+"_LOGIN_HANDLER", BaseLoginService.class);
        SaSession session = loginService.stpLogic.getSession();
        if (session.get(SaSession.USER) == null){
            session.set(SaSession.USER, loginService.getUserInfo(null));
        }
        return AjaxResult.success(session.get(SaSession.USER));
    }

    @PostMapping("/{type}/logout")
    public AjaxResult logout(@PathVariable("type")String type){
        BaseLoginService<?> loginService = SpringUtil.getBean(type.toUpperCase()+"_LOGIN_HANDLER", BaseLoginService.class);
        loginService.stpLogic.logout();
        return AjaxResult.success();
    }
}
