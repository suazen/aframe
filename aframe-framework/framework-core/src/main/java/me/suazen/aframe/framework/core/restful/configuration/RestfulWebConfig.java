package me.suazen.aframe.framework.core.restful.configuration;

import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.restful.RestfulRegister;
import me.suazen.aframe.framework.core.restful.interceptor.RestfulInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
@Configuration
@Conditional(RestfulRegister.class)
@Slf4j
public class RestfulWebConfig implements WebMvcConfigurer {
    @Value("${restful.prefix:}")
    String prefix;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("register restful interceptor");
        //注册拦截器，并设置优先级为最低
        registry.addInterceptor(new RestfulInterceptor()).order(Ordered.LOWEST_PRECEDENCE).addPathPatterns(prefix+"/**");
    }
}
