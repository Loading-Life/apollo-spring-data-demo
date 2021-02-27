package com.loading.apollo.data.springredis.factory;

import io.lettuce.core.resource.ClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class RedisConnectionFactoryManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionFactoryManager.class);

    @Resource
    private RedisProperties redisProperties;

    @Resource
    private ClientResources clientResources;

    @Resource
    private ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers;

    /**
     * 创建Redistribution连接工厂
     * @return 连接工厂
     */
    public RedisConnectionFactory createRedisConnectionFactory() {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redisProperties.getLettuce().getPool());
        return createLettuceConnectionFactory(clientConfig);
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {

        LettuceConnectionFactory connectionFactory;
        if (isSentinelMode()) {
            connectionFactory = new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
        } else if (isClusterMode()) {
            connectionFactory = new LettuceConnectionFactory(getClusterConfig(), clientConfiguration);
        } else {
            connectionFactory = new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
        }
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    private boolean isSentinelMode() {
        return null != redisProperties.getSentinel()
                && !StringUtils.isEmpty(redisProperties.getSentinel().getMaster())
                && null != redisProperties.getSentinel().getNodes()
                && redisProperties.getSentinel().getNodes().size() != 0;
    }

    private boolean isClusterMode() {
        return null != redisProperties.getCluster()
                && null != redisProperties.getCluster().getNodes()
                && redisProperties.getCluster().getNodes().size() != 0;
    }

    /**
     * sentinel mode config
     * @return sentinel config
     */
    private RedisSentinelConfiguration getSentinelConfig() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.setMaster(redisProperties.getSentinel().getMaster());
        redisProperties.getSentinel().getNodes().forEach(node ->{
            String[] valueArr = node.split(":");
            logger.info("Redis Sentinel Node Config : {}", Arrays.toString(valueArr));
            Assert.isTrue(valueArr.length > 1, "Redis Sentinel config invalid");
            String host = valueArr[0];
            String port = valueArr[1];
            config.addSentinel(new RedisNode(host, Integer.parseInt(port)));
        });
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return config;
    }

    /**
     * cluster mode config
     * @return cluster config
     */
    protected final RedisClusterConfiguration getClusterConfig() {
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        redisProperties.getCluster().getNodes().forEach(node -> {
            String[] valueArr = node.split(":");
            logger.info("Redis Cluster Node Config : {}", Arrays.toString(valueArr));
            Assert.isTrue(valueArr.length > 1, "Redis Cluster config invalid");
            String host = valueArr[0];
            String port = valueArr[1];
            config.addClusterNode(new RedisClusterNode(host, Integer.parseInt(port)));
        });
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        logger.info("Redis Cluster Password : {}", redisProperties.getPassword());
        return config;
    }

    /**
     * standalone mode config
     * @return stand config
     */
    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        logger.info("Redis Host : {}, Port : {}, Password : {}", redisProperties.getHost(), redisProperties.getPort(), redisProperties.getPassword());
        return config;
    }

    LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources, Pool pool) {
        LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        builder.clientResources(clientResources);
        customize(builder);
        return builder.build();
    }

    private LettuceClientConfigurationBuilder createBuilder(Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new PoolBuilderFactory().createBuilder(pool);
    }

    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (redisProperties.isSsl()) {
            builder.useSsl();
        }
        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        }
        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout());
            }
        }
    }

    private void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        for (LettuceClientConfigurationBuilderCustomizer customizer : builderCustomizers.getIfAvailable(Collections::emptyList)) {
            customizer.customize(builder);
        }
    }
}


