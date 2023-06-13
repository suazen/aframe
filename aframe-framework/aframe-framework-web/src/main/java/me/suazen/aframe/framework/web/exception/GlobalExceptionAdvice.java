package me.suazen.aframe.framework.web.exception;

import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.domain.AjaxResult;
import me.suazen.aframe.framework.core.exception.BusinessException;
import me.suazen.aframe.framework.core.util.ExceptionUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-04-26
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {
    @ExceptionHandler({Exception.class})
    public Object handle(Exception ex) {
        return handleAjax(ex);
    }

    public static AjaxResult handleAjax(Exception e){
        if (e instanceof BusinessException){
            String msg = ExceptionUtil.getMessage(e);
            log.error(msg);
            return AjaxResult.fail(msg);
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException)e;
            BindingResult bindingResult = ex.getBindingResult();
            String msg = bindingResult.getFieldErrors().stream()
                    .map(fieldError-> StrUtil.blankToDefault(fieldError.getDefaultMessage(),"参数\""+fieldError.getField()+"\"校验失败"))
                    .collect(Collectors.joining(", "));
            if (StrUtil.isNotBlank(msg)) {
                return AjaxResult.fail(msg);
            }
            return AjaxResult.fail("参数校验失败");
        }else if (e instanceof SaTokenException){
            return AjaxResult.fail(e.getMessage());
        }
        String msg = ExceptionUtil.getMessage(e);
        log.error(msg,e);
        return AjaxResult.fail(msg);
    }
}
