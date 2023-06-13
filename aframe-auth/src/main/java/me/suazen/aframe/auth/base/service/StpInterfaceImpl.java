package me.suazen.aframe.auth.base.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import me.suazen.aframe.framework.core.constants.Constant;
import me.suazen.aframe.system.core.entity.SysMenu;
import me.suazen.aframe.system.core.entity.SysRole;
import me.suazen.aframe.system.core.entity.SysRoleMenu;
import me.suazen.aframe.system.core.entity.SysUserRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 声明权限码集合
        List<String> permissionList = new ArrayList<>();

        // 2. 遍历角色列表，查询拥有的权限码
        for (String roleId : getRoleList(loginId, loginType)) {
            SaSession roleSession = SaSessionCustomUtil.getSessionById("role-" + roleId);
            List<String> list = roleSession.get("Permission_List", () -> queryPermListFromDb(roleId));
            permissionList.addAll(list);
        }

        // 3. 返回权限码集合
        return permissionList;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get("Role_List", () -> queryRoleListFromDb((String) loginId));
    }

    private List<String> queryPermListFromDb(String roleId){
        List<SysRoleMenu> rolePermList = new SysRoleMenu()
                .select(SysRoleMenu.MENUID)
                .roleId().eq(roleId)
                .list();
        List<String> permIdList = rolePermList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        List<SysMenu> permList = new SysMenu().select(SysMenu.PERMS)
                .menuType().eq(Constant.MENU_TYPE_ANNIU)
                .menuId().in(permIdList)
                .list();     // 从数据库查询这个角色所拥有的权限列表
        return permList.stream().map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    private List<String> queryRoleListFromDb(String userId){
        List<SysUserRole> userRoleList = new SysUserRole().select(SysUserRole.ROLEID)
                .userId().eq(userId)
                .list();
        List<String> roleIdList = userRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> roleList = new SysRole().select(SysRole.ROLEKEY)
                .roleId().in(roleIdList)
                .list();
        return roleList.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());
    }
}
