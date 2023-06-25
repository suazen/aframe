package me.suazen.aframe.core.util;

import java.util.Date;

/**
 * @author sujizhen
 * @date 2023-06-12
 **/
public class DateUtil extends cn.hutool.core.date.DateUtil {
    public static String nowSimple(){
        return format(new Date(),"yyyyMMddHHmmss");
    }
}
