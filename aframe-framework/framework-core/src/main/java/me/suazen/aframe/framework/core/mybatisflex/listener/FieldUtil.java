package me.suazen.aframe.framework.core.mybatisflex.listener;

import cn.hutool.core.util.ReflectUtil;

/**
 * @author sujizhen
 * @date 2023-04-26
 **/
class FieldUtil {
    public static void setFieldValueIfPresent(Object o,String filedName,Object value){
        if (ReflectUtil.hasField(o.getClass(),filedName)){
            ReflectUtil.setFieldValue(0,filedName,value);
        }
    }
}
