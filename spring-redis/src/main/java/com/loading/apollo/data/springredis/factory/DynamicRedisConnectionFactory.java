package com.loading.apollo.data.springredis.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

import java.util.concurrent.atomic.AtomicReference;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class DynamicRedisConnectionFactory implements RedisConnectionFactory, ApplicationContextAware {

    private final AtomicReference<RedisConnectionFactory> redisConnectionFactory;

    private ApplicationContext applicationContext;


    public DynamicRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        super();
        this.redisConnectionFactory = new AtomicReference<>(redisConnectionFactory);
    }

    public RedisConnectionFactory getAndSet(RedisConnectionFactory connectionFactory) {
        return redisConnectionFactory.getAndSet(connectionFactory);
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return redisConnectionFactory.get().translateExceptionIfPossible(ex);
    }

    @Override
    public RedisConnection getConnection() {
        return redisConnectionFactory.get().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return redisConnectionFactory.get().getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return redisConnectionFactory.get().getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return redisConnectionFactory.get().getSentinelConnection();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}

