package me.suazen.aframe.module.echo.common.constants;

/**
 * @author sujizhen
 * @date 2023-06-28
 **/
public enum MemberType {
    FREE("0","免费账号"),
    DEGREE("1","按次计费"),
    PERIOD("2","按期间付费"),
    SUPER("99","内部用户")
    ;
    final String label;
    public final String value;

    MemberType(String value, String label){
        this.label = label;
        this.value = value;
    }
}
