package me.suazen.aframe.auth.login.wxlogin.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Getter
@Setter
public class WxLoginBody {
    /**
     * 微信授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;
}
