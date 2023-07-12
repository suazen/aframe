package me.suazen.aframe.core.mybatisplus;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;

/**
 * @author sujizhen
 * @date 2023-07-12
 **/
public class WrapperUtil<E,M extends BaseMapper<E>> {
    private final BaseMapper<E> mapper;

    private WrapperUtil(Class<M> mapperClass){
        mapper = SpringUtil.getBean(mapperClass);
    }

    public static <E1,M1 extends BaseMapper<E1>> WrapperUtil<E1,M1> builder(Class<M1> mapperClass){
        return new WrapperUtil<>(mapperClass);
    }

    public QueryChainWrapper<E> query(){
        return new QueryChainWrapper<>(mapper);
    }

    public LambdaQueryChainWrapper<E> lambdaQuery(){
        return new LambdaQueryChainWrapper<>(mapper);
    }

    public UpdateChainWrapper<E> update(){
        return new UpdateChainWrapper<>(mapper);
    }

    public LambdaUpdateChainWrapper<E> lambdaUpdate(){
        return new LambdaUpdateChainWrapper<>(mapper);
    }

    public void insert(E entity){
        mapper.insert(entity);
    }
}
