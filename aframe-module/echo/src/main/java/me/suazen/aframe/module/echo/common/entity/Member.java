package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author sujizhen
 * @since 2023-06-29
 */
@Getter
@Setter
@TableName("member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.INPUT)
    private String userId;

    /**
     * 会员类型（0免费，1按次计费，2按期间付费）
     */
    private String memberType;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 可用次数
     */
    private Integer usableDegree;

    /**
     * 有效天数
     */
    private Integer expiryDate;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 更新人
     */
    private String updateBy;
}
