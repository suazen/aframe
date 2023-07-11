package me.suazen.aframe.module.echo.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
@Getter
@Setter
@TableName("prompt_setting")
public class PromptSetting implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 有效状态
     */
    private String state;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 插入位置
     */
    private String pos;
}
