package me.suazen.aframe.module.echo.wechat.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.core.exception.BusinessException;
import me.suazen.aframe.module.echo.common.entity.WxUser;
import me.suazen.aframe.module.echo.common.util.StpWxUtil;
import me.suazen.aframe.module.echo.common.util.WeChatAuthUtil;
import me.suazen.aframe.module.echo.member.service.MemberService;
import me.suazen.aframe.module.echo.wechat.dto.StateDTO;
import me.suazen.aframe.module.echo.wechat.dto.WxLoginBody;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Service("WECHAT_LOGIN_HANDLER")
@Slf4j
public class WxLoginService extends BaseLoginService<WxUser> {
    private static final String WX_LOGIN_STATE = "wx-login-state:";
    private static final String STATE_NO_LOGIN = "noLogin";
    private static final String STATE_SCANNED = "scanned";

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MemberService memberService;

    public WxLoginService(){
        this.stpLogic = StpWxUtil.stpLogic;
    }

    @Override
    protected WxUser doCheck(JSONObject loginBody) {
        WxLoginBody body = loginBody.to(WxLoginBody.class);
        if (StrUtil.isNotEmpty(body.getState())){
            RBucket<String> stateCache = redissonClient.<String>getBucket(WX_LOGIN_STATE +body.getState());
            if (StrUtil.isEmpty(stateCache.get())){
                throw new BusinessException("登录失败，二维码已失效，请刷新后重新扫码登录");
            }
            stateCache.expire(Duration.ofMinutes(3));
        }
        JSONObject wxUserInfo = WeChatAuthUtil.accessAndGetUserInfo(body.getCode());
        WxUser user = new WxUser().wxId().eq(wxUserInfo.getString("openid")).one();
        if (user == null){
            user = new WxUser();
            //注册新用户
            user.setNickname(wxUserInfo.getString("nickname"));
            user.setWxId(wxUserInfo.getString("openid"));
            user.setAvatar(wxUserInfo.getString("headimgurl"));
            mapper.insert(user);
            // 新用户默认免费会员
            memberService.newFreeMember(user.getUserId());
        } else {
            BeanUtil.copyProperties(wxUserInfo,user);
            user.setAvatar(wxUserInfo.getString("headimgurl"));
        }
        return user;
    }

    @Override
    protected void doAfterLogin(JSONObject body,WxUser user) {
        super.doAfterLogin(body,user);
        WxLoginBody loginBody = body.to(WxLoginBody.class);
        if (StrUtil.isNotBlank(loginBody.getState())) {
            redissonClient.getBucket(WX_LOGIN_STATE + loginBody.getState()).set(stpLogic.getTokenValue(),5,TimeUnit.MINUTES);
        }
    }

    /**
     * 微信扫码登录流程：
     * ①生成uuid并缓存（3分钟过期），初始状态nologin
     * ②前端轮询，通过uuid查询登录状态
     * ③扫码后更新状态为scanned，刷新缓存有效期为3分钟
     * ④授权登录后缓存token，缓存5分钟
     */
    public StateDTO checkWxAuthState(String stateCode,String wxcode){
        //入参未携带code，生成新的并返回
        if (StrUtil.isEmpty(stateCode)){
            return new StateDTO().newCode(generateCode());
        }
        //从redis获取token
        String token = redissonClient.<String>getBucket(WX_LOGIN_STATE +stateCode).get();
        //若token为空，说明redis已过期，重新生成code并返回
        if (token == null){
            return new StateDTO().newCode(generateCode());
        }
        //未登录状态
        if (STATE_NO_LOGIN.equals(token)){
            //auth-feedback页面请求
            if (StrUtil.isNotBlank(wxcode)){
                redissonClient.<String>getBucket(WX_LOGIN_STATE +stateCode).set(STATE_SCANNED+":"+wxcode,3, TimeUnit.MINUTES);
            }
            return new StateDTO();
        }
        //已扫码
        if (token.startsWith(STATE_SCANNED)){
            if (token.split(":")[1].equals(wxcode)){
                return new StateDTO();
            }
            //auth-feedback页面请求
            return new StateDTO().scanned();
        }
        //已登录，返回token
        return new StateDTO().token(token);
    }

    private String generateCode(){
        String uuid = UUID.fastUUID().toString();
        redissonClient.<String>getBucket(WX_LOGIN_STATE +uuid).set(STATE_NO_LOGIN,3, TimeUnit.MINUTES);
        return uuid;
    }
}
