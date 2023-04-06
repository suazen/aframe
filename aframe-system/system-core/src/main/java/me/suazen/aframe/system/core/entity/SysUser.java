package me.suazen.aframe.system.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user")
public class SysUser {
    /*
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;
    /*
     * 登录名称
     */
    private String loginName;
    /*
     * 用户名称
     */
    private String username;
    /*
     * 用户密码
     */
    private String password;
    /*
     * 上次登录IP
     */
    private String loginIp;
    /*
     * 上次登录时间
     */
    private String loginDate;
    /*
     * 创建者
     */
    private String creator;
    /*
     * 创建时间
     */
    private String createTime;
    /*
     * 更新者
     */
    private String updator;
    /*
     * 更新时间
     */
    private String updateTime;
}
