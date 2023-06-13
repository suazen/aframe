package me.suazen.aframe.auth.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sa-Token 持久层实现 [Redisson客户端、Redis存储、Jackson序列化]
 *
 * @author 疯狂的狮子Li
 *
 */
@Component
public class SaTokenDaoRedissonJackson implements SaTokenDao {

    /**
     * redisson 客户端
     */
    public RedissonClient redissonClient;

    /**
     * 标记：是否已初始化成功
     */
    public boolean isInit;

    @Autowired
    public void init(RedissonClient redissonClient) {
        // 不重复初始化
        if(this.isInit) {
            return;
        }

        // 指定相应的序列化方案
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        // 通过反射获取Mapper对象, 增加一些配置, 增强兼容性
        try {
            // 重写 SaSession 生成策略
            SaStrategy.me.createSession = SaSessionForJacksonCustomized::new;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // 开始初始化相关组件
        this.redissonClient = redissonClient;
        this.isInit = true;
    }


    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        return rBucket.get();
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        // 判断是否为永不过期
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set(value);
        } else {
            RBatch batch = redissonClient.createBatch();
            RBucketAsync<String> bucket = batch.getBucket(key);
            bucket.setAsync(value);
            bucket.expireAsync(Duration.ofSeconds(timeout));
            batch.execute();
        }
    }

    /**
     * 修修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        long timeout = rBucket.remainTimeToLive();
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if(expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        RBucket<String> rBucket = redissonClient.getBucket(key);
        rBucket.expire(Duration.ofSeconds(timeout));
    }



    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        RBucket<Object> rBucket = redissonClient.getBucket(key);
        return rBucket.get();
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        // 判断是否为永不过期
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.set(object);
        } else {
            RBatch batch = redissonClient.createBatch();
            RBucketAsync<Object> bucket = batch.getBucket(key);
            bucket.setAsync(object);
            bucket.expireAsync(Duration.ofSeconds(timeout));
            batch.execute();
        }

    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        long timeout = rBucket.remainTimeToLive();
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if(timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if(expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        RBucket<Object> rBucket = redissonClient.getBucket(key);
        rBucket.expire(Duration.ofSeconds(timeout));
    }


    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Stream<String> stream = redissonClient.getKeys().getKeysStreamByPattern(prefix + "*" + keyword + "*");
        List<String> list = stream.collect(Collectors.toList());
        return SaFoxUtil.searchList(list, start, size, sortType);
    }

}