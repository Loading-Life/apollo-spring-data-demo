package com.loading.apollo.data.springredis.config;

import com.loading.apollo.data.springredis.factory.DynamicRedisConnectionFactory;
import com.loading.apollo.data.springredis.factory.RedisConnectionFactoryManager;
import com.loading.apollo.data.springredis.refresher.RedisConnectionFactoryRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
@Configuration
public class ApolloSpringRedisRefreshAutoConfig {

    @Bean
    public RedisConnectionFactoryManager redisConnectionFactoryManager(){
        return new RedisConnectionFactoryManager();
    }

    @Bean
    public DynamicRedisConnectionFactory redisConnectionFactory(RedisConnectionFactoryManager redisConnectionFactoryManager) {
        RedisConnectionFactory redisConnectionFactory = redisConnectionFactoryManager.createRedisConnectionFactory();
        return new DynamicRedisConnectionFactory(redisConnectionFactory);
    }

    @Bean
    public RedisConnectionFactoryRefresher redisConnectionFactoryRefresher(){
        return new RedisConnectionFactoryRefresher();
    }

}
