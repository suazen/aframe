package me.suazen.aframe.framework.core.mybatisplus;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import me.suazen.aframe.framework.core.exception.BusinessException;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author sujizhen
 * @date 2023-06-12
 **/
@Component
public class RedisIdGenerator implements IdentifierGenerator {
    @Resource
    private RedissonClient redissonClient;

    @Override
    public Number nextId(Object entity) {
        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String bizKey = Arrays.stream(ReflectUtil.getFields(entity.getClass()))
                .filter(field-> field.getAnnotation(TableId.class) != null)
                .findFirst()
                .orElseThrow(()->new BusinessException("类"+entity.getClass().getName()+"未配置TableId注解"))
                .getName();
        //根据bizKey调用分布式ID生成
        return redissonClient.getIdGenerator("seq:"+bizKey).nextId();
    }
}
