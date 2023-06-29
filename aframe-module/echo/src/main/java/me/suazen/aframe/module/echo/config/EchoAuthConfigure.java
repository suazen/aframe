package me.suazen.aframe.module.echo.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sujizhen
 * @date 2023-06-29
 **/
@Configuration
public class EchoAuthConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler-> {
            //app端权限校验规则
            SaRouter.notMatch("/openai/**","/auth/wechat/**")
                    .check(r-> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
