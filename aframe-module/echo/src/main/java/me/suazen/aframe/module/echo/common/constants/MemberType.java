package me.suazen.aframe.module.echo.common.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public enum MemberType {
    FREE("0","免费账号",remains->"您的剩余可用次数为0次，请联系客服购买次数。" ,MemberAction.BYTIME),
    DEGREE("1","按次计费",remains->"您的剩余可用次数为0次，请联系客服购买次数。", MemberAction.BYTIME),
    PERIOD("2","按期间付费",remains->"您的套餐已过期，请联系客服购买套餐。", MemberAction.BYPERIOD),
    LIMIT_BY_DAY("3","按期间每天固定次数",remains->{
        //-1表示有效期已过期
        if (remains == -1){
            return "您的套餐已过期，请联系客服购买套餐。";
        }
        return "您今日的剩余可用次数为0次，请明日再使用。";
    },MemberAction.BYDAY),
    BETA("66","内测用户",remains->{
        if (remains == -1){
            return "内测活动已结束，请联系客服购买套餐。";
        }
        return "您今日的免费次数已用完，请明日再来体验。";
    }, MemberAction.BYDAY),
    //内部用户不限使用次数
    SUPER("99","内部用户",remains->"",MemberAction.UNLIMIT)
    ;
    @Getter
    private final String value;
    @Getter
    private final Function<Integer,String> limitMsg;
    @Getter
    private final MemberAction action;

    MemberType(String value,String label,Function<Integer,String> limitMsg,MemberAction action){
        this.value = value;
        this.limitMsg = limitMsg;
        this.action = action;
    }

    public static MemberType getByValue(String value){
        return Arrays.stream(MemberType.values())
                .filter(memberType -> memberType.value.equals(value))
                .findFirst()
                .orElse(FREE);
    }
}
