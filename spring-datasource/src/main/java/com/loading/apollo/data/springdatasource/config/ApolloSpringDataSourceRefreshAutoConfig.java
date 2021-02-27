package com.loading.apollo.data.springdatasource.config;

import com.loading.apollo.data.springdatasource.datasource.DataSourceManager;
import com.loading.apollo.data.springdatasource.datasource.DynamicDataSource;
import com.loading.apollo.data.springdatasource.refresher.DataSourceRefresher;
import com.loading.apollo.data.springdatasource.util.CustomizedConfigurationPropertiesBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * desc: 自动装载
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
@Configuration
public class ApolloSpringDataSourceRefreshAutoConfig {

    @Bean
    public DataSourceManager dataSourceManager(){
        return new DataSourceManager();
    }

    @Bean
    public DynamicDataSource dataSource(DataSourceManager dataSourceManager) {
        DataSource actualDataSource = dataSourceManager.createDataSource();
        return new DynamicDataSource(actualDataSource);
    }

    @Bean
    public DataSourceRefresher dataSourceRefresher(){
        return new DataSourceRefresher();
    }

    @Bean
    public CustomizedConfigurationPropertiesBinder customizedConfigurationPropertiesBinder(){
        return new CustomizedConfigurationPropertiesBinder();
    }

}
