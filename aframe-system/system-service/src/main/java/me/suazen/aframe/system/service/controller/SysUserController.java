package me.suazen.aframe.system.service.controller;

import me.suazen.aframe.system.core.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Resource
    private SysUserMapper sysUserMapper;

    @GetMapping("/test")
    public String test(){
        return "";
    }
}