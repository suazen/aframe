package me.suazen.aframe.module.echo.sensitive.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import me.suazen.aframe.module.echo.sensitive.service.SensitiveWordService;
import me.suazen.aframe.web.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sujizhen
 * @date 2023-07-04
 **/
@RestController
@RequestMapping("/sensitiveWord")
public class SensitiveWordController {
    @Resource
    private SensitiveWordService sensitiveWordService;

    @SaIgnore
    @GetMapping("/queryAll")
    public AjaxResult queryAll(){
        return AjaxResult.success(sensitiveWordService.queryAllWord());
    }
}
