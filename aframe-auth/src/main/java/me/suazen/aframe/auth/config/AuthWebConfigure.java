package me.suazen.aframe.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Configuration
public class AuthWebConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle-> StpUtil.checkLogin()).isAnnotation(true))
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/*/login");
    }
}
