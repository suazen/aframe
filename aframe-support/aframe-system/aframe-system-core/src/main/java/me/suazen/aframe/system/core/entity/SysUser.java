package me.suazen.aframe.system.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.suazen.aframe.system.core.base.User;

import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author sujizhen
 * @since 2023-06-12
 */
@Getter
@Setter
@TableName("sys_user")
public class SysUser implements Serializable, User {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    @JsonIgnore
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 微信id
     */
    private String wxId;

    /**
     * 性别
     */
    private String sex;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 上次登录IP
     */
    private String loginIp;

    /**
     * 上次登录时间
     */
    private String loginDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;
}
