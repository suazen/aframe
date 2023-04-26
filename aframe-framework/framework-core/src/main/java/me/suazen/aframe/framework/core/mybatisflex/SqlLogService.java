package me.suazen.aframe.framework.core.mybatisflex;

import com.mybatisflex.core.audit.AuditMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
@Service
@Slf4j
public class SqlLogService {

    @Async("sqlLogExecutor")
    public void logAsync(AuditMessage auditMessage){
        log.info("{},{}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime());
    }

}
