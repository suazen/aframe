package me.suazen.aframe.framework.core.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


@MapperScan({"me.suazen.aframe.**.mapper"})
@Configuration
public class MybatisConfig {
    
}
