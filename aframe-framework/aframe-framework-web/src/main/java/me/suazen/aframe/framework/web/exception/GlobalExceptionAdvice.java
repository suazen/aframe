package me.suazen.aframe.framework.web.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.web.constants.ResponseCode;
import me.suazen.aframe.framework.web.domain.AjaxResult;
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
            return AjaxResult.error(msg);
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException)e;
            BindingResult bindingResult = ex.getBindingResult();
            String msg = bindingResult.getFieldErrors().stream()
                    .map(fieldError-> StrUtil.blankToDefault(fieldError.getDefaultMessage(),"参数\""+fieldError.getField()+"\"校验失败"))
                    .collect(Collectors.joining(", "));
            if (StrUtil.isNotBlank(msg)) {
                return AjaxResult.of(ResponseCode.BAD_REQUEST).setMsg(msg);
            }
            return AjaxResult.of(ResponseCode.BAD_REQUEST);
        }else if (e instanceof NotLoginException){
            return AjaxResult.of(ResponseCode.UNAUTHORIZED).setMsg(e.getMessage());
        }else if (e instanceof NotPermissionException){
            return AjaxResult.of(ResponseCode.FORBIDDEN).setMsg(e.getMessage());
        }else if (e instanceof SaTokenException){
            return AjaxResult.error(e.getMessage());
        }
        String msg = ExceptionUtil.getMessage(e);
        log.error(msg,e);
        return AjaxResult.error(msg);
    }
}
