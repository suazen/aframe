package me.suazen.aframe.module.echo.promptManager.service.impl;

import cn.hutool.core.util.StrUtil;
import me.suazen.aframe.core.constants.GlobalConstant;
import me.suazen.aframe.core.util.RandomUtil;
import me.suazen.aframe.module.echo.common.constants.Constant;
import me.suazen.aframe.module.echo.common.entity.PromptSetting;
import me.suazen.aframe.module.echo.common.entity.PromptTemplate;
import me.suazen.aframe.module.echo.common.mapper.PromptSettingMapper;
import me.suazen.aframe.module.echo.common.mapper.PromptTemplateMapper;
import me.suazen.aframe.module.echo.promptManager.service.PromptService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
@Service
public class PromptServiceImpl implements PromptService {
    @Resource
    private PromptTemplateMapper promptTemplateMapper;
    @Resource
    private PromptSettingMapper promptSettingMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void savePrompt(PromptTemplate promptTemplate) {
        promptTemplate.setPromptId(null);
        promptTemplate.setState(GlobalConstant.YES);
        promptTemplateMapper.insert(promptTemplate);
    }

    @Override
    public List<String> queryPrompt(String query) {
        RList<String> prompts = redissonClient.getList(Constant.REDIS_KEY_PROMPT_TEMPLATE);
        if (!prompts.isExists()){
            List<PromptTemplate> templates = new PromptTemplate().select(PromptTemplate.CONTENT).state().eq(GlobalConstant.YES).list();
            prompts.addAll(templates.stream().map(PromptTemplate::getContent).collect(Collectors.toList()));
        }
        if (prompts.isEmpty()){
            return Collections.emptyList();
        }
        if ("all".equals(query)){
            return prompts.readAll();
        }
        if (StrUtil.isEmpty(query)){
            int length = Math.min(prompts.size(),6);
            return prompts.get(RandomUtil.randomInts(0,prompts.size()-1,length));
        }
        return prompts.readAll()
                .stream()
                .filter(hint-> {
                    String[] splits = query.split(" ");
                    for (String split : splits) {
                        if (hint.toLowerCase().contains(split.toLowerCase())){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        RList<String> prompts = redissonClient.getList(Constant.REDIS_KEY_PROMPT_TEMPLATE);
        prompts.clear();
        List<PromptTemplate> hints = new PromptTemplate().select(PromptTemplate.CONTENT).state().eq(GlobalConstant.YES).list();
        prompts.addAll(hints.stream().map(PromptTemplate::getContent).collect(Collectors.toList()));
    }

    @Override
    public void addSetting(PromptSetting promptSetting) {
        promptSetting.setState(GlobalConstant.YES);
        promptSettingMapper.insert(promptSetting);
        redissonClient.getList(Constant.REDIS_KEY_PROMPT_SETTING).clear();
    }
}
