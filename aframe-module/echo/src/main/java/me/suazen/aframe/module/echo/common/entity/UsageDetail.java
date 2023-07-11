package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author sujizhen
 * @since 2023-07-11
 */
@Getter
@Setter
@Builder
@TableName("usage_detail")
public class UsageDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String usageId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 对话id
     */
    private String conversationId;

    /**
     * 耗时（单位 秒）
     */
    private Integer costTime;

    /**
     * token数量
     */
    private Integer costTokens;

    /**
     * 开始时间
     */
    private String startTime;
}
