/**
 * 
 */
package com.github.phantomthief.stats;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author w.vela
 */
public class SimpleCounter {

    private final AtomicLong count = new AtomicLong();

    private final AtomicLong cost = new AtomicLong();

    private final long resetTime = System.currentTimeMillis();

    private void stats(long cost) {
        this.count.incrementAndGet();
        this.cost.addAndGet(cost);
    }

    public long getCount() {
        return count.get();
    }

    public long getCost() {
        return cost.get();
    }

    public long getResetTime() {
        return resetTime;
    }

    public static Consumer<SimpleCounter> statsConsumer(long cost) {
        return counter -> counter.stats(cost);
    }

    public static UnaryOperator<SimpleCounter> resetter() {
        return old -> new SimpleCounter();
    }

    public static Function<SimpleCounter, Map<String, Object>> toStatsMap() {
        return counter -> {
            Map<String, Object> r = new HashMap<>();
            long duration = System.currentTimeMillis() - counter.getResetTime();
            r.put("qps", duration <= 0 ? 0 : (double) counter.getCount() / duration * 1000);
            r.put("recordCount", counter.getCount());
            r.put("latencyTotal", counter.getCost());
            r.put("latencyAverage", (double) counter.getCost() / counter.getCount());
            r.put("lastResetTime",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(counter.getResetTime()));
            return r;
        };
    }

}
