package com.loading.apollo.data.springusecase;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
@EnableApolloConfig
@SpringBootApplication
public class SpringUseCaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringUseCaseApplication.class, args);
    }

}
