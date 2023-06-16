package me.suazen.aframe.starter.openai.controller;

import cn.hutool.core.util.StrUtil;
import me.suazen.aframe.framework.core.exception.BusinessException;
import me.suazen.aframe.framework.web.domain.AjaxResult;
import me.suazen.aframe.starter.openai.dto.ChatDTO;
import me.suazen.aframe.starter.openai.service.OpenaiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/openai")
public class OpenaiController {
    @Resource
    private OpenaiService openaiService;

    @GetMapping("/clearChat")
    public AjaxResult clearChat(String uuid){
        openaiService.clearChat(uuid);
        return AjaxResult.success();
    }

    @PostMapping(value = "/chat")
    public void chat(@RequestBody @Validated ChatDTO dto){
        openaiService.sendMessage(dto);
    }

    @GetMapping("/reGenerate")
    public void reGenerate(String uuid, Integer index){
        if (StrUtil.isEmpty(uuid)){
            throw new BusinessException("uuid不能为空");
        }
        if (index == null){
            throw new BusinessException("序号不能为空");
        }
        openaiService.reGenerate(uuid, index);
    }
}
