package me.suazen.aframe.auth.login.passwdlogin.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Getter
@Setter
public class PasswdLoginBody {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}
