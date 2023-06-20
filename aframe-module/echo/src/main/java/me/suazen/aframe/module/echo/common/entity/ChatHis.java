package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 聊天记录表
 * </p>
 *
 * @author sujizhen
 * @since 2023-06-18
 */
@Getter
@Setter
@TableName("chat_his")
public class ChatHis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String chatHisId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 对话id
     */
    private String conversationId;

    /**
     * 角色
     */
    private String role;

    /**
     * 内容
     */
    private String content;

    /**
     * 聊天时间
     */
    private String chatTime;

    /**
     * 序号
     */
    private Integer chatIndex;
}
