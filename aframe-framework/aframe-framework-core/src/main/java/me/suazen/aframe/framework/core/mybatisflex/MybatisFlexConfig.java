package me.suazen.aframe.framework.core.mybatisflex;

import cn.hutool.core.thread.ThreadUtil;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@MapperScan({"me.suazen.aframe.**.mapper"})
@Configuration
public class MybatisFlexConfig {
    @Resource
    private SqlLogService sqlLogService;

    public MybatisFlexConfig(){
        //注册主键生成器
        KeyGeneratorFactory.register("redis",new RedisKeyGenerator());

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage->sqlLogService.logAsync(auditMessage));
    }

    @Bean("sqlLogExecutor")
    public Executor sqlLogExecutor(){
        return Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("sqlLog-thread-"));
    }
}
