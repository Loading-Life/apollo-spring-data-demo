package com.loading.apollo.data.springredis.refresher;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.loading.apollo.data.springredis.factory.DynamicRedisConnectionFactory;
import com.loading.apollo.data.springredis.factory.RedisConnectionFactoryManager;
import com.loading.apollo.data.springredis.util.RedisConnectionTerminationTask;
import com.loading.apollo.data.springredis.util.RedisConnectionTerminationThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class RedisConnectionFactoryRefresher implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConnectionFactoryRefresher.class);

    private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new RedisConnectionTerminationThreadFactory());

    private ApplicationContext applicationContext;

    @Resource
    private RedisConnectionFactoryManager connectionFactoryManager;

    @Resource
    private DynamicRedisConnectionFactory dynamicRedisConnectionFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ApolloConfigChangeListener(interestedKeyPrefixes = {"spring.redis"})
    public void onChange(ConfigChangeEvent changeEvent) {
        refreshRedisConnectionFactory(changeEvent.changedKeys());
    }

    /**
     * rebind configuration beans, e.g. DataSourceProperties
     * @see org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder#onApplicationEvent
     *
     * @param changedKeys 变更的keys
     */
    private void refreshRedisConnectionFactory(Set<String> changedKeys) {
        StringBuilder builder = new StringBuilder();
        for (String r : changedKeys) {
            builder.append(r).append(",");
        }
        LOGGER.info("刷新REDIS连接，变更KEYS[{}]", builder.toString());

        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));

        RedisConnectionFactory connectionFactory = connectionFactoryManager.createRedisConnectionFactory();
        RedisConnectionFactory redisConnectionFactory = dynamicRedisConnectionFactory.getAndSet(connectionFactory);
        asyncTerminate(redisConnectionFactory);

        LOGGER.info("Finished refreshing redis source");

    }

    /**
     * 异步关闭以前的REDIS连接
     *
     * @param redisConnectionFactory redis连接工厂
     */
    private void asyncTerminate(RedisConnectionFactory redisConnectionFactory) {
        redisConnectionFactory.getConnection().close();
        RedisConnectionTerminationTask task = new RedisConnectionTerminationTask(redisConnectionFactory, scheduledExecutorService);
        scheduledExecutorService.schedule(task, 0, TimeUnit.MILLISECONDS);
    }

}
