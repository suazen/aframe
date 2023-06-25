package me.suazen.aframe.module.echo.openai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.web.domain.AjaxResult;
import me.suazen.aframe.module.echo.openai.dto.ChatDTO;
import me.suazen.aframe.module.echo.openai.service.OpenaiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/openai")
public class OpenaiController {
    @Resource
    private OpenaiService openaiService;

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

    @SaIgnore
    @GetMapping("/queryHint")
    public AjaxResult queryHint(String query){
        return AjaxResult.success(openaiService.queryHint(query));
    }
}
