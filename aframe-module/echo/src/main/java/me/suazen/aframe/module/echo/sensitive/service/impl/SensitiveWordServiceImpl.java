package me.suazen.aframe.module.echo.sensitive.service.impl;

import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.module.echo.common.constants.RedisKey;
import me.suazen.aframe.module.echo.common.entity.SensitiveWord;
import me.suazen.aframe.module.echo.common.mapper.SensitiveWordMapper;
import me.suazen.aframe.module.echo.sensitive.service.SensitiveWordService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-07-04
 **/
@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {
    @Resource
    private SensitiveWordMapper sensitiveWordMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<String> queryAllWord() {
        RList<String> cacheList = redissonClient.getList(RedisKey.sensitive_word.name());
        if (cacheList.isEmpty()){
            List<SensitiveWord> wordList = new SensitiveWord().state().eq(GlobalConstant.YES).list();
            Set<String> words = wordList
                    .stream().map(SensitiveWord::getWord)
                    .collect(Collectors.toSet());
            cacheList.addAll(words);
        }
        return cacheList.readAll();
    }
}
