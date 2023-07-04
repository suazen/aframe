package me.suazen.aframe.module.echo.openai.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import me.suazen.aframe.module.echo.openai.service.OpenaiService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SseEmitter> chat(@RequestBody @Validated ChatDTO dto){
        // 防止nginx缓存请求
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .headers(httpHeaders)
                .body(openaiService.sendMessage(dto));
    }

    @GetMapping("/reGenerate")
    public ResponseEntity<SseEmitter> reGenerate(String uuid, Integer index){
        if (StrUtil.isEmpty(uuid)){
            throw new BusinessException("uuid不能为空");
        }
        if (index == null){
            throw new BusinessException("序号不能为空");
        }
        // 防止nginx缓存请求
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .headers(httpHeaders)
                .body(openaiService.reGenerate(uuid, index));
    }
}
