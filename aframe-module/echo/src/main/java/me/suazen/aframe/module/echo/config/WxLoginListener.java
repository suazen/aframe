package me.suazen.aframe.module.echo.config;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.SaLoginModel;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.module.echo.member.service.MemberService;
import me.suazen.aframe.module.echo.common.util.StpWxUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author sujizhen
 * @date 2023-06-29
 **/
@Slf4j
@Component
public class WxLoginListener extends SaTokenListenerForSimple {
    @Resource
    private MemberService memberService;

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        if (!StpWxUtil.LOGIN_TYPE.equals(loginType)){
            return;
        }
        log.info("用户{}登录成功，token:{}",loginId,tokenValue);
        memberService.initMemberUsage((String) loginId);
    }
}
