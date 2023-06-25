package me.suazen.aframe.web.sse;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.web.sse.handler.StreamEventHandler;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SseClient {
    private final String url;

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

    public void execute(StreamEventHandler eventHandler){
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            // 这儿根据自己的情况选择get或post
            urlConnection.setRequestMethod(method);
            urlConnection.setDoOutput(doOutput);
            urlConnection.setDoInput(doInput);
            urlConnection.setUseCaches(useCache);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            //读取过期时间（很重要，建议加上）
            urlConnection.setReadTimeout(timeout);
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            this.headers.forEach(urlConnection::setRequestProperty);
            this.writeBody(urlConnection);
            InputStream inputStream = urlConnection.getInputStream();
            readStream(inputStream,eventHandler);
        }catch (IOException e){
            log.error("SSE请求失败",e);
            try {
                eventHandler.writeError(StreamUtils.copyToString(Objects.requireNonNull(urlConnection).getErrorStream(), StandardCharsets.UTF_8));
            }catch (NullPointerException | IOException ex){
                eventHandler.writeError(e.getMessage());
            }
        }
    }

    private void readStream(InputStream inputStream,StreamEventHandler eventHandler) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            // 处理数据接口
            eventHandler.readStream(line);
        }
        eventHandler.onComplete();
        reader.close();
    }

    /**
     * 写body
     * @param conn
     */
    private void writeBody(HttpURLConnection conn) throws IOException{
        if (StrUtil.isEmpty(this.body)){
            return;
        }
        log.info("body：{}",this.body);
        byte[] dataBytes = this.body.getBytes();
        conn.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        OutputStream os = conn.getOutputStream();
        os.write(dataBytes);
        os.flush();
        os.close();
    }
}
