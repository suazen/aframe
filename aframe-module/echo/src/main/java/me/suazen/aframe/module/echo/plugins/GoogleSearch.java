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
@Component("GoogleSearch_PLUGIN")
public class GoogleSearch implements IPlugin {

    private static final String API_KEY = "AIzaSyD2RAuvcMvVaANm-Lece6Q-RoNrWopJzEs";

    private static final String CSE_ID = "458d68388d8684f30";

    private static final String RES_NUM = "2";

    private static final String url = "https://www.googleapis.com/customsearch/v1?key="+API_KEY+"&cx="+CSE_ID+"&q=%s&num="+RES_NUM;

    @Override
    public List<ChatMessage> run(String query) {
        String encodeQuery;
        try {
             encodeQuery = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodeQuery = query;
        }
        String response = HttpUtil.createGet(String.format(url, encodeQuery))
                .execute()
                .body();
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.botMsg("我需要你提供搜索引擎的搜索结果以便总结汇总答案"));
            JSONObject resJson = JSON.parseObject(response);
            if (!resJson.containsKey("items")){
                return Collections.emptyList();
            }
            List<SearchResult> results = resJson.getJSONArray("items")
                    .stream()
                    .map(obj-> BeanUtil.toBean(obj,SearchResult.class))
                    .collect(Collectors.toList());
            messages.add(ChatMessage.userMsg(JSON.toJSONString(results)));
            return messages;
        }catch (JSONException e){
            throw new BasePluginException("调用谷歌搜索插件失败",e);
        }
    }

    @Override
    public MetaInfo metaInfo(String param) {

        return MetaInfo.builder()
                .avatar("https://www.google.com/images/branding/googleg/1x/googleg_standard_color_128dp.png")
                .name("Google Search")
                .description(String.format("谷歌搜索：%s",param))
                .build();
    }

    @Getter
    @Setter
    private static class SearchResult {
        private String title;
        private String snippet;
    }
}
