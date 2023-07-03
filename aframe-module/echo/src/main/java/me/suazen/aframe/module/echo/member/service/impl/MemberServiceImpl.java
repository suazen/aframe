package me.suazen.aframe.module.echo.member.service.impl;

import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.Constant;
import me.suazen.aframe.module.echo.common.constants.MemberAction;
import me.suazen.aframe.module.echo.common.constants.MemberType;
import me.suazen.aframe.module.echo.common.entity.Member;
import me.suazen.aframe.module.echo.common.mapper.MemberMapper;
import me.suazen.aframe.module.echo.member.service.MemberService;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author sujizhen
 * @date 2023-06-29
 **/
@Service
public class MemberServiceImpl implements MemberService {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MemberMapper memberMapper;

    @Override
    public void newFreeMember(String userId) {
        Member member = new Member();
        member.setUserId(userId);
        member.setMemberType(MemberType.FREE.getValue());
        member.setStartTime(DateUtil.nowSimple());
        member.setUsableDegree(10);
        member.setExpiryDate(0);
        memberMapper.insert(member);
    }

    @Override
    public int initMemberUsage(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(Constant.REDIS_KEY_REMAINS_TIME+userId);
        times.set(updateMemberUsage(userId));
        times.expire(Duration.ofHours(12));
        return (int) times.get();
    }

    @Override
    public RAtomicLong getUsageFromRedis(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(Constant.REDIS_KEY_REMAINS_TIME+userId);
        if (!times.isExists()){
            times.set(updateMemberUsage(userId));
            times.expire(Duration.ofHours(12));
        }
        return times;
    }

    private int updateMemberUsage(String userId) {
        Member member = memberMapper.selectById(userId);
        if (member == null){
            throw new BusinessException("操作失败，未获取到用户的会员信息");
        }
        return MemberType.getByValue(member.getMemberType())
                .getAction()
                .usageCalc(member);
    }

}
