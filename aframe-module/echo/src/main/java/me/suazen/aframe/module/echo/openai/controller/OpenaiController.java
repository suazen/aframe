package me.suazen.aframe.module.echo.openai.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import me.suazen.aframe.module.echo.openai.service.OpenaiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/openai")
public class OpenaiController {
    @Resource
    private OpenaiService openaiService;

    @PostMapping(value = "/chat")
    public SseEmitter chat(@RequestBody @Validated ChatDTO dto){
        return openaiService.sendMessage(dto);
    }

    @GetMapping("/reGenerate")
    public SseEmitter reGenerate(String uuid, Integer index){
        if (StrUtil.isEmpty(uuid)){
            throw new BusinessException("uuid不能为空");
        }
        if (index == null){
            throw new BusinessException("序号不能为空");
        }
        return openaiService.reGenerate(uuid, index);
    }
}
