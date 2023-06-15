package me.suazen.aframe.starter.gpt.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import me.suazen.aframe.starter.common.util.AzureOpenaiUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/gpt")
public class GptController {

    @SaIgnore
    @GetMapping(value = "/test",produces = "text/event-stream")
    public void test(String content){
        AzureOpenaiUtil.callStream(content);
    }
}
