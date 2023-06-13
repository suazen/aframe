package me.suazen.aframe.auth.login.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.framework.core.domain.AjaxResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            return AjaxResult.fail("Method Not Found");
        }
        return AjaxResult.success(loginService.login(loginBody));
    }

    @PostMapping("/logout")
    public AjaxResult logout(){
        StpUtil.logout();
        return AjaxResult.success();
    }
}
