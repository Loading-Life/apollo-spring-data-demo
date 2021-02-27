package com.loading.apollo.data.springdatasource.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public class DataSourceTerminationThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public DataSourceTerminationThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "apollo-datasource-termination-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}
