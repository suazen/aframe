package me.suazen.aframe.framework.web.sse;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.web.sse.exception.SseClientException;
import me.suazen.aframe.framework.web.sse.handler.StreamEventHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SseClient {
    private final String url;

    private InputStream sseInputStream;

    private String method;

    private final Map<String,String> headers = new HashMap<>();

    private boolean doOutput = true;

    private boolean doInput = true;

    private boolean useCache = false;

    private int timeout = 60 * 1000;

    private String body;

    private SseClient(String url){
        this.url = url;
    }

    public static SseClient build(String url){
        return new SseClient(url);
    }

    public SseClient method(String method){
        this.method = method;
        return this;
    }

    public SseClient body(String body){
        this.body = body;
        return this;
    }

    public SseClient header(String key,String val){
        this.headers.put(key,val);
        return this;
    }

    public SseClient header(Map<String,String> header){
        if (header != null){
            this.headers.putAll(header);
        }
        return this;
    }

    public SseClient timeout(int timeout){
        this.timeout = timeout;
        return this;
    }

    public SseClient doOutput(boolean doOutput){
        this.doOutput = doOutput;
        return this;
    }

    public SseClient doInput(boolean doInput){
        this.doInput = doInput;
        return this;
    }

    public SseClient useCache(boolean useCache){
        this.useCache = useCache;
        return this;
    }

    public SseClient execute(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 这儿根据自己的情况选择get或post
            urlConnection.setRequestMethod(method);
            urlConnection.setDoOutput(doOutput);
            urlConnection.setDoInput(doInput);
            urlConnection.setUseCaches(useCache);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            //读取过期时间（很重要，建议加上）
            urlConnection.setReadTimeout(timeout);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            this.headers.forEach(urlConnection::setRequestProperty);
            this.writeBody(urlConnection);
            InputStream inputStream = urlConnection.getInputStream();
            this.sseInputStream = new BufferedInputStream(inputStream);
        }catch (IOException e){
            log.error("SSE连接建立失败");
            throw new SseClientException("SSE连接建立失败："+e.getMessage(),e);
        }
        return this;
    }

    public void readStream(StreamEventHandler eventHandler){
        if (this.sseInputStream == null){
            throw new SseClientException("请先调用execute方法");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.sseInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理数据接口
                eventHandler.readStream(line);
            }
            eventHandler.onComplete();
            reader.close();
        } catch (IOException e) {
            log.error("SSE数据流读取失败");
            throw new SseClientException("SSE数据流读取失败："+e.getMessage(),e);
        }
    }

    /**
     * 写body
     * @param conn
     */
    private void writeBody(HttpURLConnection conn) {
        if (StrUtil.isEmpty(this.body)){
            return;
        }
        try {
            byte[] dataBytes = this.body.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
            OutputStream os = conn.getOutputStream();
            os.write(dataBytes);
            os.flush();
            os.close();
        } catch(Exception e) {
            log.error("SSE请求Body写入失败");
            throw new SseClientException("SSE请求Body写入失败："+e.getMessage(),e);
        }
    }
}
