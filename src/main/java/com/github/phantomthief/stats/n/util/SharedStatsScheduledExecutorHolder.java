/**
 * 
 */
package com.github.phantomthief.stats.n.util;

import static java.lang.Thread.MIN_PRIORITY;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author w.vela
 */
public class SharedStatsScheduledExecutorHolder {

    private static final int THREAD_COUNT = 10;

    private SharedStatsScheduledExecutorHolder() {
    }

    public static ScheduledExecutorService getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {

        private static final ScheduledExecutorService INSTANCE = newScheduledThreadPool(
                THREAD_COUNT,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-stats-helper-%d")
                        .setPriority(MIN_PRIORITY)
                        .setDaemon(true)
                        .build());
    }

}
