/**
 * 
 */
package com.github.phantomthief.stats.n.impl;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.phantomthief.stats.n.DurationStats;
import com.github.phantomthief.stats.n.MultiDurationStats;
import com.github.phantomthief.stats.n.counter.Duration;

final class SimpleMultiDurationStats<K, V extends Duration> implements
                                                                   MultiDurationStats<K, V> {

    private final ConcurrentMap<K, DurationStats<V>> map = new ConcurrentHashMap<>();
    private final Supplier<DurationStats<V>> statsFactory;

    SimpleMultiDurationStats(Supplier<DurationStats<V>> statsFactory) {
        this.statsFactory = statsFactory;
    }

    @Override
    public void stat(K key, Consumer<V> statsFunction) {
        map.computeIfAbsent(key, k -> statsFactory.get()).stat(statsFunction);
    }

    @Override
    public Map<K, Map<Long, V>> getStats() {
        return map.entrySet().stream().collect(toMap(Entry::getKey, e -> e.getValue().getStats()));
    }
}