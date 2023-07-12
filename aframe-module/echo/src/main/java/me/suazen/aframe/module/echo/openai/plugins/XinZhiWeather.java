package me.suazen.aframe.module.echo.openai.plugins;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.openai.plugins.base.IPlugin;
import me.suazen.aframe.module.echo.openai.plugins.base.MetaInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sujizhen
 * @date 2023-07-11
 **/
@Component("Weather_PLUGIN")
public class XinZhiWeather implements IPlugin {
    @Value("${plugin.weather.api-key}")
    private String apiKey;

    @Override
    public List<ChatMessage> run(String param) {
        String location = getLocation(param);
        String response = HttpUtil.createGet("https://api.seniverse.com/v3/weather/daily.json?language=zh-Hans&unit=c"
                        + "&key="+apiKey
                        + "&location="+location)
                .execute()
                .body();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.systemPrompt("Answer the question according to the weather info. Respond in Chinese. Current time: "+ DateUtil.now()));
        messages.add(ChatMessage.systemPrompt("Weather Info: "+response));
        messages.add(ChatMessage.userMsg(StrUtil.blankToDefault(JSON.parseObject(param).getString("question"),location+"的天气怎么样？")));
        return messages;
    }

    @Override
    public MetaInfo metaInfo(String param) {
        return MetaInfo.builder()
                .avatar("/image/weather.png")
                .name("Weather")
                .description(String.format("心知天气：%s", getLocation(param)))
                .build();
    }

    private String getLocation(String param){
        JSONObject paramObj = JSON.parseObject(param);
        return paramObj.getString("location");
    }
}
