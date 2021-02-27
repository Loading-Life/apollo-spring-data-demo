package com.loading.apollo.data.springdatasource.refresher;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.loading.apollo.data.springdatasource.datasource.DataSourceManager;
import com.loading.apollo.data.springdatasource.datasource.DynamicDataSource;
import com.loading.apollo.data.springdatasource.util.DataSourceTerminationTask;
import com.loading.apollo.data.springdatasource.util.DataSourceTerminationThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.Set;
import java.util.concurrent.*;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class DataSourceRefresher implements ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(DataSourceRefresher.class);

  private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new DataSourceTerminationThreadFactory());

  @Autowired
  private DynamicDataSource dynamicDataSource;

  @Autowired
  private DataSourceManager dataSourceManager;

  @Autowired
  private ApplicationContext applicationContext;

  @ApolloConfigChangeListener(interestedKeyPrefixes = "spring.datasource.")
  public void onChange(ConfigChangeEvent changeEvent) {
    refreshDataSource(changeEvent.changedKeys());
  }

  private synchronized void refreshDataSource(Set<String> changedKeys) {
    try {
      logger.info("Refreshing data source");

      /**
       * rebind configuration beans, e.g. DataSourceProperties
       * @see org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder#onApplicationEvent
       */
      this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));

      DataSource newDataSource = dataSourceManager.createAndTestDataSource();
      DataSource oldDataSource = dynamicDataSource.setDataSource(newDataSource);
      asyncTerminate(oldDataSource);

      logger.info("Finished refreshing data source");
    } catch (Throwable ex) {
      logger.error("Refreshing data source failed", ex);
    }
  }

  private void asyncTerminate(DataSource dataSource) {
    DataSourceTerminationTask task = new DataSourceTerminationTask(dataSource, scheduledExecutorService);

    //start now
    scheduledExecutorService.schedule(task, 0, TimeUnit.MILLISECONDS);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}

