/**
 * 
 */
package com.github.phantomthief.stats.n.profiler;

import java.util.concurrent.atomic.AtomicLong;

import com.github.phantomthief.stats.n.profiler.anntation.Max;
import com.github.phantomthief.stats.n.profiler.anntation.Sum;
import com.github.phantomthief.stats.n.profiler.stats.Stats;

/**
 * @author w.vela
 */
@Sum
public class SimpleStats implements Stats {

    private final AtomicLong count;
    @Max
    private volatile long maxCost;

    public SimpleStats(long count, long maxCost) {
        this.count = new AtomicLong(count);
        this.maxCost = maxCost;
    }

    public long getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(long maxCost) {
        this.maxCost = maxCost;
    }

    public long getCount() {
        return count.get();
    }

    public void setCount(long count) {
        this.count.set(count);
    }

    @Override
    public String toString() {
        return "SimpleStats [count=" + count + ", maxCost=" + maxCost + "]";
    }
}
