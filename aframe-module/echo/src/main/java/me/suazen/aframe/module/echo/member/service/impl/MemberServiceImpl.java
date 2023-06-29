package me.suazen.aframe.module.echo.member.service.impl;

import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.Constant;
import me.suazen.aframe.module.echo.common.constants.MemberType;
import me.suazen.aframe.module.echo.common.entity.ChatHis;
import me.suazen.aframe.module.echo.common.entity.Member;
import me.suazen.aframe.module.echo.common.mapper.MemberMapper;
import me.suazen.aframe.module.echo.member.service.MemberService;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
        member.setMemberType(MemberType.FREE.value);
        member.setStartTime(DateUtil.nowSimple());
        member.setUsableDegree(10);
        memberMapper.insert(member);
    }

    @Override
    public int initMemberUsage(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(Constant.REDIS_KEY_REMAINS_TIME+userId);
        times.set(updateMemberUsage(userId));
        return (int) times.get();
    }

    @Override
    public RAtomicLong getUsageFromRedis(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(Constant.REDIS_KEY_REMAINS_TIME+userId);
        if (!times.isExists()){
            times.set(updateMemberUsage(userId));
        }
        return times;
    }

    private int updateMemberUsage(String userId) {
        Member member = memberMapper.selectById(userId);
        if (member == null){
            throw new BusinessException("操作失败，未获取到用户会员信息");
        }
        //内部用户不限次数
        if (member.getMemberType().equals(MemberType.SUPER.value)){
            return 9999;
        }
        //如果是按期间付费，则判断是否过期
        if (member.getMemberType().equals(MemberType.PERIOD.value)){
            //开始时间加上有效期大于当前日期则表示已过期
            if (DateUtil.offsetDay(DateUtil.parse(member.getStartTime()),member.getExpiryDate()).compareTo(new Date())>0) {
                return 0;
            }else {
                return 99;
            }
        }
        //按次计费，计算并更新剩余次数
        long answerCount = new ChatHis()
                .userId().eq(userId)
                .role().eq("assistant")
                .chatTime().ge(member.getStartTime())
                .count();
        if (answerCount == 0){
            return member.getUsableDegree();
        }
        member.setUsableDegree(member.getUsableDegree()-(int)answerCount);
        member.setStartTime(DateUtil.nowSimple());
        memberMapper.updateById(member);
        return member.getUsableDegree();
    }

}
