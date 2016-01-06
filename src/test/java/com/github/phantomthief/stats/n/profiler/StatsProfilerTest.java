/**
 * 
 */
package com.github.phantomthief.stats.n.profiler;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.github.phantomthief.stats.n.profiler.stats.Stats;
import com.github.phantomthief.stats.n.profiler.stats.StatsKey;
import com.github.phantomthief.stats.n.profiler.util.BufferStatsProfilerHelper;

/**
 * @author w.vela
 */
public class StatsProfilerTest {

    private Map<StatsKey<?>, Stats> map = new ConcurrentHashMap<>();

    private BufferStatsProfilerHelper bufferStatsProfilerHelper = new BufferStatsProfilerHelper(100, //
            SECONDS.toMillis(3), //
            (time, key, stats) -> {
                System.out.println(time + "," + key + "," + stats);
                map.put(key, stats);
            });

    @Test
    public void test() throws Exception {
        long sum1 = 0;
        for (int i = 0; i < 100; i++) {
            sum1 += i;
            bufferStatsProfilerHelper.stats(TestStatsKey.biz1, new SimpleStats(i, i));
        }
        long sum2 = 0;
        for (int i = 0; i < 200; i++) {
            sum2 += i;
            bufferStatsProfilerHelper.stats(TestStatsKey.biz2, new SimpleStats(i, i));
        }
        sleepUninterruptibly(3, SECONDS);
        SimpleStats stats1 = (SimpleStats) map.get(TestStatsKey.biz1);
        System.out.println(stats1);
        SimpleStats stats2 = (SimpleStats) map.get(TestStatsKey.biz2);
        System.out.println(stats2);
        assertEquals(stats1.getCount(), sum1);
        assertEquals(stats1.getMaxCost(), 99);
        assertEquals(stats2.getCount(), sum2);
        assertEquals(stats2.getMaxCost(), 199);
    }
}
