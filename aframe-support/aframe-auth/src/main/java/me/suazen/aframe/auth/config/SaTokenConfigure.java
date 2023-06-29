package me.suazen.aframe.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import me.suazen.aframe.web.constants.ResponseCode;
import me.suazen.aframe.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sujizhen
 * @date 2023-06-06
 **/
@Configuration
public class SaTokenConfigure {
    @Resource
    private List<String> saIgnoreList;

    @Autowired
    public void configSaToken(SaTokenConfig config) {
        config.setTokenName("token")               // token名称 (同时也是cookie名称)
                .setTimeout(24 * 60 * 60)        // token有效期，单位s 1天
                .setActivityTimeout(4 * 60 * 60)         // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒 4小时
                .setIsConcurrent(true)               // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
                .setIsShare(false)                   // 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
                .setTokenStyle("uuid")               // token风格
                .setIsReadCookie(false)
                .setIsPrint(false)
                .setIsLog(false);                    // 是否输出操作日志
    }

    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定 拦截路由 与 放行路由
                .addInclude("/**")

                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 登录认证 -- 拦截所有路由，并排除登录
                    SaRouter.match("/**")
                            .notMatch("/auth/*/login", "/actuator/**")
                            .notMatch(saIgnoreList)
                            .check(()->{
                                if (SaManager.stpLogicMap.values().stream().noneMatch(StpLogic::isLogin)){
                                    throw NotLoginException.newInstance(null,null);
                                }
                            });

                    //actuator接口添加http basic认证
                    SaRouter.match("/actuator/**", () -> SaBasicUtil.check());

                    // 更多拦截处理方式，请参考“路由拦截式鉴权”章节 */
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    if (e instanceof NotLoginException){
                        return AjaxResult.of(ResponseCode.UNAUTHORIZED).setMsg(e.getMessage());
                    }else if (e instanceof NotPermissionException){
                        return AjaxResult.of(ResponseCode.FORBIDDEN).setMsg(e.getMessage());
                    }else{
                        return AjaxResult.error(e.getMessage());
                    }
                })
                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("aframe-server")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                    ;
                });
    }
}
