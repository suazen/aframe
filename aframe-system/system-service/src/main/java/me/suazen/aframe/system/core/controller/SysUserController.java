package me.suazen.aframe.system.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.suazen.aframe.framework.core.restful.RestfulRegister;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @GetMapping("/test")
    public String test(){
        String sql = RestfulRegister.getRestfulSql(RequestMethod.GET, "/sysUser");
        System.out.println(sql);
        return sql;
    }
}