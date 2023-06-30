package me.suazen.aframe.module.echo.common.exception;

import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.common.constants.MemberType;

/**
 * @author sujizhen
 * @date 2023-06-30
 **/
public class UsageLimitException extends BusinessException {
    public UsageLimitException(String memberType,int remains){
        super(MemberType.getByValue(memberType).getLimitMsg().apply(remains));
    }
}
