package me.suazen.aframe.auth.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sujizhen
 * @date 2023-06-25
 **/
@Component
@Slf4j

public class WhiteListInitializer implements CommandLineRunner {
    /**
     * 上下文
     */
    @Autowired
    private WebApplicationContext applicationContext;

    @Bean
    public List<String> saIgnoreList(){
        return new ArrayList<>();
    }

    @Override
    public void run(String... args) {
        //获取controller相关bean
        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
        //获取method
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        List<String> whiteList = applicationContext.getBean("saIgnoreList",List.class);
        methodMap.forEach((info,method)->{
            if (method.getMethod().isAnnotationPresent(SaIgnore.class)){
                whiteList.addAll(new ArrayList<>(info.getDirectPaths()));
            }
        });
    }
}
