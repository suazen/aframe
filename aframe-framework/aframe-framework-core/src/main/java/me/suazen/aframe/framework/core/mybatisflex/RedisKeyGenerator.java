package me.suazen.aframe.framework.core.mybatisflex;

import cn.hutool.extra.spring.SpringUtil;
import com.mybatisflex.core.keygen.IKeyGenerator;
import org.redisson.api.RedissonClient;

/**
 * @author sujizhen
 * @date 2023-04-25
 **/
public class RedisKeyGenerator implements IKeyGenerator {
    @Override
    public Object generate(Object o, String s) {
        return SpringUtil.getBean(RedissonClient.class).getIdGenerator(s).nextId();
    }
}
