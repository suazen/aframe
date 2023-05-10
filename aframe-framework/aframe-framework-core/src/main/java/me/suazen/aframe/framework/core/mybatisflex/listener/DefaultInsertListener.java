package me.suazen.aframe.framework.core.mybatisflex.listener;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.mybatisflex.annotation.InsertListener;

import java.util.Date;

/**
 * @author sujizhen
 * @date 2023-04-26
 **/
public class DefaultInsertListener implements InsertListener {
    @Override
    public void onInsert(Object o) {
        String now = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        FieldUtil.setFieldValueIfPresent(o,"createTime",now);
        FieldUtil.setFieldValueIfPresent(o,"updateTime",now);
    }
}
