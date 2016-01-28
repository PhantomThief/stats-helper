/**
 * 
 */
package com.github.phantomthief.stats.n.impl;

import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.function.Consumer;

import com.github.phantomthief.stats.n.DurationStats;
import com.github.phantomthief.stats.n.counter.Duration;
import com.github.phantomthief.util.MoreSuppliers.CloseableSupplier;

/**
 * @author w.vela
 */
class LazyDurationStats<V extends Duration> implements DurationStats<V> {

    private final CloseableSupplier<DurationStats<V>> factory;

    LazyDurationStats(CloseableSupplier<DurationStats<V>> factory) {
        this.factory = factory;
    }

    @Override
    public void stat(Consumer<V> statsFunction) {
        factory.get().stat(statsFunction);
    }

    @Override
    public Map<Long, V> getStats() {
        return factory.map(DurationStats::getStats).orElse(emptyMap());
    }

    @Override
    public void close() throws Exception {
        factory.tryClose(DurationStats::close);
    }
}
