package me.suazen.aframe.web.sse;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sujizhen
 * @date 2023-07-03
 **/
@Slf4j
public class SseClient {
    private final String url;

    private String method;

    private String body;

    private final Map<String,String> headers = new HashMap<>();

    private SseClient(String url){
        this.url = url;
    }

    public static SseClient create(String url){
        return new SseClient(url);
    }

    public SseClient method(String method){
        this.method = method;
        return this;
    }

    public SseClient header(String key, String val){
        this.headers.put(key,val);
        return this;
    }

    public SseClient header(Map<String,String> header){
        if (header != null){
            this.headers.putAll(header);
        }
        return this;
    }

    public SseClient body(String body){
        this.body = body;
        return this;
    }

    public void execute(EventSourceListener listener){
        log.info(this.body);
        Request request = new Request.Builder()
                .url(this.url)
                .header("Connection", "Keep-Alive")
                .header("Content-Type", "application/json;charset=UTF-8")
                .headers(Headers.of(this.headers))
                .method(this.method, RequestBody.create(this.body, MediaType.get("application/json;charset=UTF-8")))
                .build();
        //创建事件
        EventSources.createFactory(SpringUtil.getBean(OkHttpClient.class)).newEventSource(request, listener);
    }
}
