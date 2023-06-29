package me.suazen.aframe.module.echo.prompt.service;

import me.suazen.aframe.module.echo.common.entity.PromptSetting;
import me.suazen.aframe.module.echo.common.entity.PromptTemplate;

import java.util.List;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public interface PromptService {

    void savePrompt(PromptTemplate promptTemplate);

    List<String> queryPrompt(String query);

    void refresh();

    void addSetting(PromptSetting promptSetting);
}
