package me.suazen.aframe.web.constants;

/**
 * @author sujizhen
 * @date 2023-06-15
 **/
public enum ResponseCode {
    SUCCESS(200,"操作成功"),
    BAD_REQUEST(400,"参数校验失败"),
    UNAUTHORIZED(401,"未登录"),
    FORBIDDEN(403,"权限不足"),
    ERROR(500,"系统异常"),
    ;
    private int code;

    private String msg;

    ResponseCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
