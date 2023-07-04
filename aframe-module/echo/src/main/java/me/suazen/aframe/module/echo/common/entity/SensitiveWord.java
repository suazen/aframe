package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author sujizhen
 * @since 2023-07-04
 */
@Getter
@Setter
@TableName("sensitive_word")
public class SensitiveWord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String wordId;

    /**
     * 敏感词
     */
    private String word;

    /**
     * 状态
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
