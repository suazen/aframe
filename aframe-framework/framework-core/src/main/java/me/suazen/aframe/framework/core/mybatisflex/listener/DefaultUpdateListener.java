package me.suazen.aframe.framework.core.mybatisflex.listener;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.mybatisflex.annotation.UpdateListener;

import java.util.Date;

/**
 * @author sujizhen
 * @date 2023-04-26
 **/
public class DefaultUpdateListener implements UpdateListener {
    @Override
    public void onUpdate(Object o) {
        String now = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        FieldUtil.setFieldValueIfPresent(o,"updateTime",now);
    }

}
