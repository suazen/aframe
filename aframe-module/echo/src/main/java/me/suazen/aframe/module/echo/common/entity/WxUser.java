package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import me.suazen.aframe.system.core.base.User;

import java.io.Serializable;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
@Getter
@Setter
@TableName("wx_user")
public class WxUser implements Serializable, User {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 微信id
     */
    private String wxId;

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
