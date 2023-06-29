package me.suazen.aframe.module.echo.prompt.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import me.suazen.aframe.module.echo.common.entity.PromptSetting;
import me.suazen.aframe.module.echo.common.entity.PromptTemplate;
import me.suazen.aframe.module.echo.prompt.service.PromptService;
import me.suazen.aframe.web.domain.AjaxResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
@RestController
@RequestMapping("/prompt")
public class PromptController {
    @Resource
    private PromptService promptService;

    @SaIgnore
    @GetMapping("/query")
    public AjaxResult promptList(String query){
        return AjaxResult.success(promptService.queryPrompt(query));
    }

    @PostMapping("/save")
    public AjaxResult savePrompt(@RequestBody @Validated PromptTemplate promptTemplate){
        promptService.savePrompt(promptTemplate);
        return AjaxResult.success();
    }

    @GetMapping("/refresh")
    public AjaxResult refresh(){
        promptService.refresh();
        return AjaxResult.success();
    }

    @PostMapping("/addSetting")
    public AjaxResult addSetting(@RequestBody PromptSetting promptSetting){
        promptService.addSetting(promptSetting);
        return AjaxResult.success();
    }
}
