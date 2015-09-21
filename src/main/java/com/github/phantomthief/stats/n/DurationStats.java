/**
 * 
 */
package com.github.phantomthief.stats.n;

import java.util.Map;
import java.util.function.Consumer;

import com.github.phantomthief.stats.n.counter.Duration;

/**
 * @author w.vela
 */
public interface DurationStats<V extends Duration> {

    public void stat(Consumer<V> statsFunction);

    public Map<Long, V> getStats();

}
