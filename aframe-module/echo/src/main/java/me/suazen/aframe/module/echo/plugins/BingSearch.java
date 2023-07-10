package me.suazen.aframe.module.echo.plugins;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import me.suazen.aframe.module.echo.common.dto.ChatMessage;
import me.suazen.aframe.module.echo.plugins.base.IPlugin;
import me.suazen.aframe.module.echo.plugins.base.MetaInfo;
import me.suazen.aframe.module.echo.plugins.exception.BasePluginException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-07-10
 **/
@Component("BingSearch_PLUGIN")
public class BingSearch implements IPlugin {
    @Value("${plugin.bing.api-key}")
    private String apiKey;

    @Value("${plugin.bing.end-point}")
    private String endPoint;

    @Override
    public List<ChatMessage> run(String param) {
        String query = getQueryString(param);
        String encodeQuery;
        try {
            encodeQuery = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodeQuery = query;
        }
        String response = HttpUtil.createGet(endPoint+"/v7.0/search?mkt=zh-cn&count=5&q="+encodeQuery)
                .header("Ocp-Apim-Subscription-Key",apiKey)
                .execute()
                .body();
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.systemPrompt("You are an AI assistant that helps people find information. Respond using markdown. Post the relate links after the information."));
            messages.add(ChatMessage.userMsg("搜索"+query));
            messages.add(ChatMessage.botMsg("I need you provide the result of search engine to get summary"));
            JSONObject resJson = JSON.parseObject(response);
            if (!resJson.containsKey("webPages")){
                return Collections.emptyList();
            }
            if (!resJson.getJSONObject("webPages").containsKey("value")){
                return Collections.emptyList();
            }
            List<SearchResult> results = resJson.getJSONObject("webPages").getJSONArray("value")
                    .stream()
                    .map(obj-> BeanUtil.toBean(obj, SearchResult.class))
                    .collect(Collectors.toList());
            messages.add(ChatMessage.userMsg(JSON.toJSONString(results)));
            return messages;
        }catch (JSONException e){
            throw new BasePluginException("调用必应搜索插件失败",e);
        }
    }

    @Override
    public MetaInfo metaInfo(String param) {
        return MetaInfo.builder()
                .avatar("/src/assets/image/bing.png")
                .name("Bing Search")
                .description(String.format("Bing搜索：%s",getQueryString(param)))
                .build();
    }

    private String getQueryString(String param){
        JSONObject paramObj = JSON.parseObject(param);
        return paramObj.getString("query");
    }

    @Getter
    @Setter
    private static class SearchResult{
        private String name;
        private String url;
        private String snippet;
    }
}
