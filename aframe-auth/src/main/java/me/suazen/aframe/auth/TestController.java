package me.suazen.aframe.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sujizhen
 * @date 2023-05-10
 **/
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/v1")
    public String v1(){
        return "hello world";
    }
}
