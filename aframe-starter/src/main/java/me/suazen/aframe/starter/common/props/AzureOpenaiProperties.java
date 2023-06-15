package me.suazen.aframe.starter.common.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("openai.azure")
public class AzureOpenaiProperties {
    private String endPoint;

    private String model;

    private String version;

    private String apiKey;
}
