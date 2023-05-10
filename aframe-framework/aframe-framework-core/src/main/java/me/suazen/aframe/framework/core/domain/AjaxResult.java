package me.suazen.aframe.framework.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

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

    private AjaxResult(Status status){
        this.code = status.code;
        this.msg = status.msg;
    }

    public static AjaxResult success(){
        return new AjaxResult(Status.SUCCESS);
    }

    public static AjaxResult success(Object data){
        return AjaxResult.success().setData(data);
    }

    public static AjaxResult fail(){
        return new AjaxResult(Status.FAIL);
    }

    public static AjaxResult fail(String msg){
        return AjaxResult.fail().setMsg(msg);
    }

    @AllArgsConstructor
    public enum Status {
        SUCCESS(0,"操作成功"),
        FAIL(1,"操作失败")
        ;

        final int code;
        final String msg;
    }
}
