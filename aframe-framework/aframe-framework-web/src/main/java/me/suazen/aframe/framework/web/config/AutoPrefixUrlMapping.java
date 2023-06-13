package me.suazen.aframe.framework.web.config;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sujizhen
 * @date 2023-06-13
 **/
public class AutoPrefixUrlMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo requestMappingInfo = super.getMappingForMethod(method, handlerType);
        if(null != requestMappingInfo){
            //获取url前缀
            String prefix = getPrefix(handlerType);
            //根据url前缀生成RequestMappingInfo并与原有的RequestMappingInfo合并
            return RequestMappingInfo.paths(prefix).build().combine(requestMappingInfo);
        }
        return null;
    }

    private String getPrefix(Class<?> handlerType){
        String packageName = handlerType.getPackage().getName();    //获取控制器所在包路径
        String prefix = "";
        Pattern p = Pattern.compile("aframe.([a-zA-Z]+)");  //路径匹配正则
        Matcher m = p.matcher(packageName);
        if (m.find()){
            prefix = m.group(1);
        }
        return prefix;
    }
}