package me.suazen.aframe.web.sse;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.core.util.DateUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.function.Consumer;

/**
 * @author sujizhen
 * @date 2023-06-26
 **/
@Slf4j
public class SseServer {
    private final SseEmitter sseEmitter;

    private static final ThreadPoolTaskExecutor executor = SpringUtil.getBean("threadPoolTaskExecutor");

    private final long startTime = new Date().getTime();

    private SseServer(){
        this.sseEmitter = new SseEmitter();
        this.initDefault();
    }

    private SseServer(long timeout){
        this.sseEmitter = new SseEmitter(timeout);
        this.initDefault();
    }

    private void initDefault(){
        this.onCompletion(()-> log.info("SSE传输完毕，耗时 {}",DateUtil.formatBetween(new Date().getTime()-startTime, BetweenFormatter.Level.SECOND)));
        this.onTimeout(this.sseEmitter::complete);
        this.onError((e)->{
            log.error("SSE传输异常",e);
            sseEmitter.completeWithError(e);
        });
    }

    public SseServer onCompletion(Runnable runnable){
        this.sseEmitter.onCompletion(runnable);
        return this;
    }

    public SseServer onTimeout(Runnable runnable){
        this.sseEmitter.onTimeout(runnable);
        return this;
    }

    public SseServer onError(Consumer<Throwable> onError){
        this.sseEmitter.onError(onError);
        return this;
    }

    public SseServer onProcess(Consumer<SseEmitter> onProcess){
        executor.execute(()-> {
            try {
                onProcess.accept(this.sseEmitter);
            }catch (Exception e){
                log.error("SSE数据传输过程执行异常",e);
                this.sseEmitter.completeWithError(e);
            }
        });
        return this;
    }

    public SseEmitter build(){
        return sseEmitter;
    }

    public static SseServer builder(){
        return new SseServer();
    }

    public static SseServer builder(long timeout){
        return new SseServer(timeout);
    }

}
