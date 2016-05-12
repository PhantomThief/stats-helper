/**
 * 
 */
package com.github.phantomthief.stats.n;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.github.phantomthief.stats.n.counter.SimpleCounter;
import com.github.phantomthief.stats.n.impl.SimpleDurationStats;

/**
 * @author w.vela
 */
public class SimpleDurationStatsTest {

    @Test
    public void test() throws Exception {
        DurationStats<SimpleCounter> durationStats = SimpleDurationStats.newBuilder().build();
        for (int i = 0; i < 10; i++) {
            durationStats.stat(SimpleCounter.stats(10));
        }
        sleepUninterruptibly(10, SECONDS);
        for (int i = 0; i < 10; i++) {
            durationStats.stat(SimpleCounter.stats(10));
        }
        Map<Long, SimpleCounter> stats = durationStats.getStats();
        assertEquals(stats.get(SECONDS.toMillis(10)).getCost(), 100);
        assertEquals(stats.get(SECONDS.toMillis(10)).getCount(), 10);
        assertEquals(stats.get(MINUTES.toMillis(1)).getCost(), 200);
        assertEquals(stats.get(MINUTES.toMillis(1)).getCount(), 20);
        System.out.println(stats);
    }
}
