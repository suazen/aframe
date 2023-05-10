package me.suazen.aframe.system.core.mapper;

import com.mybatisflex.core.BaseMapper;
import me.suazen.aframe.system.core.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("select * from sys_user where username = #{username}")
    SysUser queryByUsername(@Param("username")String username);
}
