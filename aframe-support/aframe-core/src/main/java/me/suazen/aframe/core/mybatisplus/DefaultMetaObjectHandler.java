package me.suazen.aframe.core.mybatisplus;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String now = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        this.strictInsertFill(metaObject, "createTime", () -> now, String.class);
        this.strictInsertFill(metaObject, "updateTime", () -> now, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String now = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        this.strictInsertFill(metaObject, "updateTime", () -> now, String.class);
    }
}
