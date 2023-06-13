package me.suazen.aframe.auth.login.wxlogin.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import me.suazen.aframe.auth.base.service.BaseLoginService;
import me.suazen.aframe.auth.login.wxlogin.dto.WxLoginBody;
import me.suazen.aframe.auth.login.wxlogin.util.WeChatAuthUtil;
import me.suazen.aframe.system.core.entity.SysUser;
import org.springframework.stereotype.Service;
/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Service("WECHAT_LOGIN_HANDLER")
public class WxLoginService extends BaseLoginService {

    @Override
    protected SysUser doCheck(JSONObject loginBody) {
        WxLoginBody body = loginBody.to(WxLoginBody.class);
        JSONObject wxUserInfo = WeChatAuthUtil.accessAndGetUserInfo(body.getCode());
        SysUser sysUser = new SysUser().wxId().eq(wxUserInfo.getString("openid")).one();
        if (sysUser == null){
            //do register
            sysUser = BeanUtil.toBean(wxUserInfo,SysUser.class);
            sysUser.setWxId(wxUserInfo.getString("openid"));
            sysUser.setAvatar(wxUserInfo.getString("headimgurl"));
            sysUserMapper.insert(sysUser);
        } else {
            BeanUtil.copyProperties(wxUserInfo,sysUser);
            sysUser.setAvatar(wxUserInfo.getString("headimgurl"));
        }
        return sysUser;
    }
}
