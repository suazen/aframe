package me.suazen.aframe.module.echo.common.constants;

/**
 * @author sujizhen
 * @date 2023-07-04
 **/
public enum RedisKey {
    openai_prompt_template,
    openai_prompt_setting,
    sensitive_word
    ;


    public enum Folder {
        openai_chat_his,
        openai_remains_time
        ;
        public String key(String key){
            return this.name()+":"+key;
        }
    }
}
