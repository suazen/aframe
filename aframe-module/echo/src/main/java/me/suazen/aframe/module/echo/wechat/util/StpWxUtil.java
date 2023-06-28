package me.suazen.aframe.module.echo.wechat.util;

import cn.dev33.satoken.stp.StpLogic;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public class StpWxUtil {
    public static final String LOGIN_TYPE = "wechat";

    public static StpLogic stpLogic = new StpLogic(LOGIN_TYPE);
}
