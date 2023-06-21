package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 提示模板表
 * </p>
 *
 * @author sujizhen
 * @since 2023-06-21
 */
@Getter
@Setter
@TableName("chat_hint")
public class ChatHint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String hintId;

    /**
     * 角色
     */
    private String role;

    /**
     * 场景
     */
    private String scene;

    /**
     * 内容
     */
    private String content;

    /**
     * 有效状态
     */
    private String state;

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
