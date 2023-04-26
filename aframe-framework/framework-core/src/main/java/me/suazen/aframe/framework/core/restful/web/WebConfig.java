package me.suazen.aframe.framework.core.restful.web;

import me.suazen.aframe.framework.core.restful.interceptor.RestfulInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器，并设置优先级为最低
        registry.addInterceptor(new RestfulInterceptor()).order(Ordered.LOWEST_PRECEDENCE);
    }
}
