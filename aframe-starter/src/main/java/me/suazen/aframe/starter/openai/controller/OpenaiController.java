package me.suazen.aframe.starter.openai.controller;

import me.suazen.aframe.framework.web.domain.AjaxResult;
import me.suazen.aframe.starter.openai.service.OpenaiService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/openai")
public class OpenaiController {
    @Resource
    private OpenaiService openaiService;

    @GetMapping("/clearChat")
    public AjaxResult clearChat(){
        openaiService.clearChat();
        return AjaxResult.success();
    }

    @PostMapping(value = "/chat")
    public void chat(@RequestBody String content){
        openaiService.sendMessage(content);
    }
}
