package com.loading.apollo.data.springredis.factory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class PoolBuilderFactory {

    public LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(Pool properties) {
        return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
    }

    private GenericObjectPoolConfig getPoolConfig(Pool properties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }
}