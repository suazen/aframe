package me.suazen.aframe.system.core.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.suazen.aframe.framework.core.mybatisflex.listener.DefaultInsertListener;
import me.suazen.aframe.framework.core.mybatisflex.listener.DefaultUpdateListener;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_user",onInsert = DefaultInsertListener.class,onUpdate = DefaultUpdateListener.class)
public class SysUser implements Serializable {

    /**
     * 用户id
     */
    @Id(keyType = KeyType.Generator, value = "uuid")
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 上次登录IP
     */
    private String loginIp;

    /**
     * 上次登录时间
     */
    private String loginDate;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private String updateTime;

}
