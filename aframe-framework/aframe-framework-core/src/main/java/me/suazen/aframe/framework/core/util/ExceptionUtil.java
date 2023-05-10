package me.suazen.aframe.framework.core.util;

import io.lettuce.core.RedisException;
import me.suazen.aframe.framework.core.exception.BaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisSystemException;

import java.util.Optional;

/**
 * @author sujizhen
 * @date 2022-06-30
 **/
public class ExceptionUtil extends cn.hutool.core.exceptions.ExceptionUtil {
    public static String getMessage(Throwable e){
        return getMessage(e,true);
    }

    public static String getMessage(Throwable e,boolean showPosition){
        String message = Optional.ofNullable(Optional.ofNullable(e.getCause())
                        .orElse(e)
                        .getMessage()).orElse("")
                .replaceAll("\r\n"," ")
                .replaceAll("\r"," ")
                .replaceAll("\n"," ")
                .replaceAll("\\s+"," ");
        String prefix = "未知异常：";
        if (e instanceof NullPointerException){
            prefix = "空指针异常";
        }else if (e instanceof BaseException){
            return message;
        }else if (e instanceof RedisSystemException || e instanceof RedisException){
            prefix = "Redis异常";
        } else if (e instanceof DataAccessException){
            prefix = "SQL异常：";
        }else if (e instanceof RuntimeException){
            prefix = "运行时异常：";
        }
        String position = showPosition?(";异常发生在："+getStackInfo(e.getStackTrace())):"";
        return prefix + message + position;
    }

    public static String getStackInfo(StackTraceElement[] stackTraceElements){
        StackTraceElement stackTrace = stackTraceElements[0];
        for (StackTraceElement stack : stackTraceElements) {
            if (!stack.toString().startsWith("me.suazen.aframe")
                    ||stack.getClassName().equals(ExceptionUtil.class.getName())){
                continue;
            }
            stackTrace = stack;
            break;
        }
        String[] classNameSplit = stackTrace.getClassName().split("\\.");
        String methodName = stackTrace.getMethodName();
        String fileName = stackTrace.getFileName();
        int lineNumber = stackTrace.getLineNumber();
        return String.format("%s.%s\n(%s:%d)",classNameSplit[classNameSplit.length-1],methodName,fileName,lineNumber);
    }
}
