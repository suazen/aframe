package me.suazen.aframe.module.echo.config.props;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.ModelType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public Encoding tokensEncoding(){
        return Encodings.newDefaultEncodingRegistry().getEncodingForModel(ModelType.GPT_3_5_TURBO);
    }
}
