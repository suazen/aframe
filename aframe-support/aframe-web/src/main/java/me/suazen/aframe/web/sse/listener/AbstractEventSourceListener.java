package me.suazen.aframe.web.sse.listener;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sujizhen
 * @date 2023-07-03
 **/
@Slf4j
public abstract class AbstractEventSourceListener extends EventSourceListener {
    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        String msg = "机器人开小差了，请重试一下吧~";
        if (t != null) {
            log.error("调用Azure接口失败", t);
        }else if (response != null) {
            try {
                if (response.body() != null) {
                    log.error("调用Azure接口失败，返回body：{}",response.body().string());
                }
            } catch (Exception e) {
                log.error("获取response.body失败", e);
            }
        }
        afterFailure(msg);
        eventSource.cancel();
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.info("Azure接口连接成功");
    }

    public abstract void afterFailure(String msg);
}
