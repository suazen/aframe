package me.suazen.aframe.module.echo.config.tasker;

import cn.hutool.core.date.DateUnit;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.entity.UsageDetail;
import me.suazen.aframe.module.echo.common.mapper.UsageDetailMapper;

import java.util.Date;
import java.util.TimerTask;

/**
 * @author sujizhen
 * @date 2023-07-11
 **/
@AllArgsConstructor
public class SaveUsageDetailTask extends TimerTask {

    private UsageDetail usageDetail;

    @Override
    public void run() {
        usageDetail.setCostTime((int)DateUtil.between(
                DateUtil.parse(usageDetail.getStartTime()),
                new Date(),
                DateUnit.SECOND
        ));
        SpringUtil.getBean(UsageDetailMapper.class).insert(usageDetail);
    }
}
