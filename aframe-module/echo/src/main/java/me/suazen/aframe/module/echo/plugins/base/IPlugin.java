package me.suazen.aframe.module.echo.plugins.base;

import me.suazen.aframe.module.echo.common.dto.ChatMessage;

import java.util.List;

/**
 * @author sujizhen
 * @date 2023-07-10
 **/
public interface IPlugin {
    List<ChatMessage> run(String param);

    MetaInfo metaInfo(String param);
}
