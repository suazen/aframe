package me.suazen.aframe.module.echo.wechat.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import me.suazen.aframe.module.echo.wechat.service.WxLoginService;
import me.suazen.aframe.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/wechat")
public class WechatController {
    @Resource
    private WxLoginService wxLoginService;

    @SaIgnore
    @GetMapping("/state")
    public AjaxResult state(String code,String scanned){
        return AjaxResult.success(wxLoginService.checkWxAuthState(code,scanned));
    }
}
