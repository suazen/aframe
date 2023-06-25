package me.suazen.aframe.auth.login.wxlogin.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author sujizhen
 * @date 2023-02-16
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties("wechat")
public class WeChatProperties {
    private String appId;

    private String secret;
}
