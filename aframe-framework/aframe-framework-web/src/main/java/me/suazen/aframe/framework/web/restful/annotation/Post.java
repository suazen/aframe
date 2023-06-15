package me.suazen.aframe.framework.web.restful.annotation;

import cn.hutool.http.ContentType;
import me.suazen.aframe.framework.web.domain.AjaxResult;
import me.suazen.aframe.framework.web.domain.IResult;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {
    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

    ContentType contentType() default ContentType.JSON;

    Class<? extends IResult> returnType() default AjaxResult.class;
}
