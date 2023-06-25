package me.suazen.aframe.auth.login.wxlogin.dto;

import me.suazen.aframe.core.constants.GlobalConstant;

import java.util.HashMap;

public class StateDTO extends HashMap<String,String> {

    public StateDTO newCode(String code){
        this.put("code",code);
        return this;
    }

    public StateDTO token(String token){
        this.put("token",token);
        return this;
    }

    public StateDTO scanned(){
        this.put("scanned", GlobalConstant.YES);
        return this;
    }
}
