package me.suazen.aframe.core.base.pojo;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public interface User {
    String getUserId();

    String getUsername();

    void setLoginIp(String ip);

    void setLoginDate(String date);
}
