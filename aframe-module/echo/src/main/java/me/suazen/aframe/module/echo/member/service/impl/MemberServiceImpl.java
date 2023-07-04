package me.suazen.aframe.module.echo.member.service.impl;

import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.constants.MemberAction;
import me.suazen.aframe.module.echo.common.constants.MemberType;
import me.suazen.aframe.module.echo.common.constants.RedisKey;
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
        member.setMemberType(MemberType.SUPER.getValue());
        member.setStartTime(DateUtil.nowSimple());
        memberMapper.insert(member);
    }

    @Override
    public void updateMemberInfo(Member member) {
        Member originMember = memberMapper.selectById(member.getUserId());
        if (originMember == null){
            throw new BusinessException("操作失败，未获取到该用户的会员信息");
        }
        if (!Arrays.stream(MemberType.values())
                .map(MemberType::getValue)
                .collect(Collectors.toList())
                .contains(member.getMemberType())){
            throw new BusinessException("操作失败，未知的会员类型");
        }
        //参数校验
        MemberType.getByValue(member.getMemberType()).getAction().upgrade(member);
        MemberType originType = MemberType.getByValue(originMember.getMemberType());
        int remains = originType.getAction().usageCalc(originMember);
        //每天固定次数计费类型的还未到期不允许变更套餐
        if (originType.getAction() == MemberAction.BYDAY && remains >= 0){
            if (!originType.getValue().equals(member.getMemberType())){
                throw new BusinessException("您的套餐还未到期，请到期后再变更套餐类型");
            }
        }

    }

    @Override
    public int initMemberUsage(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(RedisKey.Folder.openai_remains_time.key(userId));
        times.set(updateMemberUsage(userId));
        times.expire(Duration.ofHours(12));
        return (int) times.get();
    }

    @Override
    public RAtomicLong getUsageFromRedis(String userId) {
        RAtomicLong times = redissonClient.getAtomicLong(RedisKey.Folder.openai_remains_time.key(userId));
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
