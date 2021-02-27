package com.loading.apollo.data.springusecase.test;

import com.loading.apollo.data.springusecase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
@Component
public class TestOutPut implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    System.err.println("线程datasource：" + userRepository.findById(1).get().getName());

                    RedisClientInfo redisClientInfo = redisTemplate.getClientList().get(0);
                    System.err.println("线程redis：" + redisClientInfo.toString());
                    System.err.println("线程redis：" + redisTemplate.opsForValue().get("test"));

                    TimeUnit.SECONDS.sleep(2);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
    }







}
