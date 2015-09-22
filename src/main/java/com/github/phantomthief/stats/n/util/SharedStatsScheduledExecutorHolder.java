/**
 * 
 */
package com.github.phantomthief.stats.n.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author w.vela
 */
public class SharedStatsScheduledExecutorHolder {

    private static final int THREAD_COUNT = 10;

    private static class LazyHolder {

        private static final ScheduledExecutorService INSTANCE = Executors.newScheduledThreadPool(
                THREAD_COUNT,
                new ThreadFactoryBuilder() //
                        .setNameFormat("scheduled-stats-helper-%d") //
                        .setPriority(Thread.MIN_PRIORITY) //
                        .setDaemon(true) //
                        .build());
    }

    private SharedStatsScheduledExecutorHolder() {
    }

    public static ScheduledExecutorService getInstance() {
        return LazyHolder.INSTANCE;
    }

}
