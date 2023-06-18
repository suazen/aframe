package me.suazen.aframe.auth.login.wxlogin.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.suazen.aframe.framework.core.exception.BusinessException;

/**
 * @author sujizhen
 * @date 2023-05-12
 **/
@Slf4j
public class WeChatAuthUtil {
    private static final WeChatProperties properties = SpringUtil.getBean(WeChatProperties.class);

    public static JSONObject accessAndGetUserInfo(String code){
        return getUserInfo(getAccessToken(code));
    }

    public static JSONObject getAccessToken(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token"
                + "?appid="+properties.getAppId()
                + "&secret="+properties.getSecret()
                + "&code="+code
                + "&grant_type=authorization_code";
        return callWxApi(url);
    }

    public static JSONObject getUserInfo(JSONObject tokenRes){
        String url = "https://api.weixin.qq.com/sns/userinfo"
                + "?access_token=" + tokenRes.getString("access_token")
                + "&openid=" + tokenRes.getString("openid")
                + "&lang=zh_CN";
        return callWxApi(url);
    }

    private static JSONObject callWxApi(String url){
        String resStr = HttpUtil.createPost(url)
                .timeout(10000)
                .execute()
                .body();
        try {
            JSONObject resJson = JSONObject.parseObject(resStr);
            if (resJson.containsKey("errcode")){
                throw new BusinessException("微信授权登录失败，失败原因为："+resJson.getString("errmsg"));
            }
            return resJson;
        }catch (JSONException e){
            log.error("微信授权登录失败",e);
            throw new BusinessException("微信授权登录失败");
        }
    }
}
