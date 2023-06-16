package me.suazen.aframe.framework.web.domain;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.suazen.aframe.framework.web.constants.ResponseCode;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author sujizhen
 * @date 2023-04-24
 **/
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class AjaxResult implements IResult,Serializable {
    private int code;

    private Object data;

    private String msg;


    @Override
    public Object onSuccess(Object data) {
        return success(data);
    }

    private AjaxResult(ResponseCode status){
        this.code = status.getCode();
        this.msg = status.getMsg();
        this.data = null;
    }

    public static AjaxResult success(){
        return new AjaxResult(ResponseCode.SUCCESS);
    }

    public static AjaxResult success(Object data){
        return AjaxResult.success().setData(data);
    }

    public static AjaxResult error(){
        return new AjaxResult(ResponseCode.ERROR);
    }

    public static AjaxResult error(String msg){
        return AjaxResult.error().setMsg(msg);
    }

    public static AjaxResult of(ResponseCode code){
        return new AjaxResult(code);
    }

    public AjaxResult addData(String key,Object data){
        if (this.data == null){
            this.data = new JSONObject();
        }
        ((JSONObject)this.data).put(key,data);
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
