package me.suazen.aframe.module.echo.member.service;

import me.suazen.aframe.module.echo.common.entity.Member;
import org.redisson.api.RAtomicLong;

/**
 * @author sujizhen
 * @date 2023-06-29
 **/
public interface MemberService {
    void newFreeMember(String userId);

    void updateMemberInfo(Member member);

    int initMemberUsage(String userId);

    RAtomicLong getUsageFromRedis(String userId);
}
