package me.suazen.aframe.system.core.base;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public interface User {
    String getUserId();

    void setLoginIp(String ip);

    void setLoginDate(String date);
}
