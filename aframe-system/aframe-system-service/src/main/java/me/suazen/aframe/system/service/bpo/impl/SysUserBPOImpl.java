package me.suazen.aframe.system.service.bpo.impl;

import me.suazen.aframe.system.core.entity.SysUser;
import me.suazen.aframe.system.core.mapper.SysUserMapper;
import me.suazen.aframe.system.service.bpo.SysUserBPO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
@Service
public class SysUserBPOImpl implements SysUserBPO {
    @Resource
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void Register() {
        SysUser user = new SysUser();
        user.setUsername("admin");
        redissonClient.getBucket("user:1").set(user);
        redissonClient.getRemoteService("aframe").register(SysUserBPO.class,this);
    }

    @Override
    public SysUser rpc(String username) {
        SysUserBPO user = redissonClient.getRemoteService("aframe").get(SysUserBPO.class);
        return (SysUser) redissonClient.getBucket("sysUser").get();
    }

    @Override
    public SysUser queryByUsername(String username,String loginName) {
        return sysUserMapper.queryByUsername(username);
    }
}
