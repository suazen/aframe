package me.suazen.aframe.module.echo.plugins.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sujizhen
 * @date 2023-07-10
 **/
@Getter
@Setter
@Builder
public class MetaInfo {
    private String avatar;

    private String name;

    private String description;
}
