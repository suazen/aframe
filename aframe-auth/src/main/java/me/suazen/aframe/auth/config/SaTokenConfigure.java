package me.suazen.aframe.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Configuration
public class SaTokenConfigure {
    @Bean
    @Primary
    public SaTokenConfig saTokenConfig() {
        return new SaTokenConfig()
                .setTokenName("token")               // token名称 (同时也是cookie名称)
                .setTimeout(2 * 60 * 60)        // token有效期，单位s 1天
                .setActivityTimeout(30 * 60)         // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒 30分钟
                .setIsConcurrent(true)               // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
                .setIsShare(false)                   // 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
                .setTokenStyle("uuid")               // token风格
                .setIsReadCookie(false)
                .setIsPrint(false)
                .setIsLog(false);                    // 是否输出操作日志
    }
}
