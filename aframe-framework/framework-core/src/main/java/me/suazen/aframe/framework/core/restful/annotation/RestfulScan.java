package me.suazen.aframe.framework.core.restful.annotation;

import me.suazen.aframe.framework.core.restful.RestfulRegister;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author sujizhen
 * @date 2023-04-06
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(RestfulRegister.class)
public @interface RestfulScan {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};
}
