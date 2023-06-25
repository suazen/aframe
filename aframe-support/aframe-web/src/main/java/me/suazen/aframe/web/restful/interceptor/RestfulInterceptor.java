package me.suazen.aframe.web.restful.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.web.exception.GlobalExceptionAdvice;
import me.suazen.aframe.web.domain.AjaxResult;
import me.suazen.aframe.web.domain.IResult;
import me.suazen.aframe.core.util.ExceptionUtil;
import me.suazen.aframe.web.restful.domain.RestMapping;
import me.suazen.aframe.web.restful.RestfulRegister;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
@Slf4j
@Component
public class RestfulInterceptor implements HandlerInterceptor {
    private static final String CONTENT_TYPE_JSON = ContentType.JSON.toString(Charset.defaultCharset());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            RequestMethod method = RequestMethod.valueOf(request.getMethod());
            String path = request.getServletPath();
            RestMapping restMapping = RestfulRegister.getRestMapping(method,path);
            //restMapping中获取不到对应的路由则跳过，进去controller层，否则由本拦截器接管
            if (restMapping == null){
                return true;
            }
            //初始化接口入参
            Object[] params = initParams(restMapping,request,response);
            try {
                Object callResult = ReflectUtil.invoke(SpringUtil.getBean(restMapping.getClz()),restMapping.getMethod(),params);
                IResult res = ReflectUtil.newInstanceIfPossible(restMapping.getReturnType());
                writeJSON(response,res.onSuccess(callResult), restMapping.getContentType());
            }catch (UtilException ex){
                throw ex;
            } catch (Exception e){
                writeJSON(response, GlobalExceptionAdvice.handleAjax(e),CONTENT_TYPE_JSON);
            }
            return false;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            writeJSON(response,AjaxResult.error(ExceptionUtil.getMessage(e,false)),CONTENT_TYPE_JSON);
            return false;
        }
    }

    private Object[] initParams(RestMapping restMapping,HttpServletRequest request,HttpServletResponse response){
        Map<String, Object> paramMap = new HashMap<>(ServletUtil.getParamMap(request));
        String body = ServletUtil.getBody(request);
        if (StrUtil.isNotBlank(body)){
            paramMap.putAll(JSONObject.parse(body));
        }
        return Arrays.stream(restMapping.getMethod().getParameters()).map(parameter -> {
            if (parameter.getType().isAssignableFrom(HttpServletRequest.class)){
                return request;
            }
            if (parameter.getType().isAssignableFrom(HttpServletResponse.class)){
                return response;
            }
            if (paramMap.containsKey(parameter.getName())){
                return Convert.convert(parameter.getType(),paramMap.get(parameter.getName()));
            }
            if (BeanUtil.isBean(parameter.getType())){
                return BeanUtil.mapToBean(paramMap,parameter.getType(),false, CopyOptions.create().ignoreCase());
            }
            return null;
        }).toArray();
    }

    private void writeJSON(HttpServletResponse response,Object obj,String contentType){
        ServletUtil.write(response, JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue), contentType);
    }
}
