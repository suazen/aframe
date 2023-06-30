package me.suazen.aframe.module.echo.common.constants;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUnit;
import lombok.Getter;
import me.suazen.aframe.auth.base.dto.UserInfo;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.core.util.DateUtil;
import me.suazen.aframe.module.echo.common.entity.ChatHis;
import me.suazen.aframe.module.echo.common.entity.Member;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author sujizhen
 * @date 2023-06-30
 **/
public enum MemberAction {
    BYTIME("按次计算",member -> {
        //按次计费，计算并更新剩余次数
        long answerCount = new ChatHis()
                .userId().eq(member.getUserId())
                .role().eq("assistant")
                .chatTime().ge(member.getStartTime())
                .count();
        if (answerCount == 0){
            return member.getUsableDegree();
        }
        member.setUsableDegree(Math.max(0,member.getUsableDegree()-(int)answerCount));
        member.setStartTime(DateUtil.nowSimple());
        member.setUpdateTime(DateUtil.nowSimple());
        member.update();
        return member.getUsableDegree();
    }, member -> {
       //按次计费 usageDegree字段不能为空
       if (member.getUsableDegree() == null){
           throw new BusinessException("操作失败，可用次数不能为空");
       }
    }),
    BYPERIOD("按期间计算",member -> {
        //如果是按期间付费，则判断是否过期
        if (member.getExpiryDate() == 0){
            return 0;
        }
        //计算开始日期与当前日期间隔天数
        int remainDays = 0;
        int between = (int) DateUtil.between(DateUtil.parse(member.getStartTime()),new Date(), DateUnit.DAY);
        member.setStartTime(DateUtil.nowSimple());
        member.setUpdateTime(DateUtil.nowSimple());
        member.setExpiryDate(Math.max(0,member.getExpiryDate() - between));
        //如果剩余可用次数不为0或不为空，则更新用户类型为按次计费继续使用剩余次数
        if (member.getExpiryDate() == 0) {
            if (MemberType.PERIOD.getValue().equals(member.getMemberType())
                    && member.getUsableDegree() != null
                    && member.getUsableDegree() > 0){
                member.setMemberType(MemberType.DEGREE.getValue());
                remainDays = member.getUsableDegree();
            }
        }else {
            remainDays = 99;
        }
        member.update();
        return remainDays;
    },member -> {
        //按期间计费 expiryDate不能为空
        if (member.getExpiryDate() == null){
            throw new BusinessException("操作失败，有效天数不能为空");
        }
    }),
    BYDAY("每天固定次数", member -> {
        //先校验有效天数是否过期
        if (BYPERIOD.usageCalc.apply(member) == 0){
            return -1;
        }
        String nowDate = DateUtil.format(new Date(),"yyyyMMdd");
        long answerCount = new ChatHis()
                .userId().eq(member.getUserId())
                .role().eq("assistant")
                .chatTime().between(nowDate+"000000",nowDate+"235959")
                .count();
        return Math.max(0,member.getUsableDegree()-(int) answerCount);
    },member -> {
        //每天固定次数，可用次数和有效天数都不能为空
        if (member.getUsableDegree() == null){
            throw new BusinessException("操作失败，可用次数不能为空");
        }
        if (member.getExpiryDate() == null){
            throw new BusinessException("操作失败，有效天数不能为空");
        }
    }),
    UNLIMIT("无限制",member -> 9999,member -> {
        if (!"1".equals(StpUtil.getLoginId())) {
            throw new BusinessException("操作失败，内部用户只能由超级管理员设置");
        }
        member.setUpdateTime(DateUtil.nowSimple());
        member.setUpdateBy(((UserInfo<?>) StpUtil.getSession().get(SaSession.USER)).getUser().getUsername());
        member.update();
    })
    ;

    @Getter
    private final Function<Member,Integer> usageCalc;
    @Getter
    private final Consumer<Member> upgrade;

    MemberAction(String info, Function<Member,Integer> usageCalc,Consumer<Member> upgrade){
        this.usageCalc = usageCalc;
        this.upgrade = upgrade;
    }
}
