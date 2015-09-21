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
public interface MultiDurationStats<K, V extends Duration> {

    public void stat(K key, Consumer<V> statsFunction);

    public Map<K, Map<Long, V>> getStats();
}
