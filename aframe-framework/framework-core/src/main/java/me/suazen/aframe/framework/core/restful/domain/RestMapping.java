package me.suazen.aframe.framework.core.restful.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author sujizhen
 * @date 2023-04-07
 **/
@Getter
@Setter
@AllArgsConstructor
public class RestMapping implements Serializable {
    /**
     * 接口对应类目
     */
    private Class<?> clz;

    /**
     * 接口对应方法名
     */
    private Method method;

    /**
     * 接口媒体类型
     */
    private String contentType;

    /**
     * 接口返回类型
     */
    private Class<? extends IResult> returnType;
}
