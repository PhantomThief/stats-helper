/**
 * 
 */
package com.github.phantomthief.stats.n.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.phantomthief.stats.SharedStatsScheduledExecutorHolder;
import com.github.phantomthief.stats.n.DurationStats;
import com.github.phantomthief.stats.n.counter.Duration;
import com.github.phantomthief.stats.n.counter.SimpleCounter;
import com.github.phantomthief.stats.n.util.DurationStatsUtils;
import com.github.phantomthief.stats.n.util.SimpleDurationFormatter;
import com.google.common.base.Preconditions;

/**
 * @author w.vela
 */
public class SimpleDurationStats<V extends Duration> implements DurationStats<V>, AutoCloseable {

    private final Map<Long, V> counters = new ConcurrentHashMap<>();
    private final Set<Long> statsDurations;
    private final long duration;
    private final Function<Long, V> counterFactory;
    private final BinaryOperator<V> counterMerger;
    private final ScheduledFuture<?> cleanupScheduledFuture;

    private SimpleDurationStats(Set<Long> statsDurations, long duration,
            Function<Long, V> counterFactory, BinaryOperator<V> counterMerger) {
        long maxTimePeriod = statsDurations.stream().max(Comparator.naturalOrder()).get();
        this.statsDurations = statsDurations;
        this.duration = duration;
        this.counterFactory = counterFactory;
        this.counterMerger = counterMerger;
        this.cleanupScheduledFuture = SharedStatsScheduledExecutorHolder.getInstance()
                .scheduleWithFixedDelay(() -> {
                    long now = System.currentTimeMillis();
                    Iterator<Entry<Long, V>> iterator = counters.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<Long, V> entry = iterator.next();
                        if (now - entry.getKey() > maxTimePeriod) {
                            iterator.remove();
                        }
                    }
                } , 1, 1, TimeUnit.MINUTES);
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.stats.n.DurationStats#stat(java.util.function.Consumer)
     */
    @Override
    public void stat(Consumer<V> statsFunction) {
        long timePoint = System.currentTimeMillis() / duration * duration;
        V counter = counters.computeIfAbsent(timePoint, t -> counterFactory.apply(duration));
        statsFunction.accept(counter);
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.stats.n.DurationStats#getStats()
     */
    @Override
    public Map<Long, V> getStats() {
        Map<Long, V> result = new HashMap<>();
        long now = System.currentTimeMillis();
        counters.forEach((d, counter) -> {
            statsDurations.forEach(s -> {
                if (now - d <= s) {
                    result.merge(s, counter, counterMerger);
                }
            });
        });
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        cleanupScheduledFuture.cancel(false);
    }

    public static final class Builder {

        private final Set<Long> statsDurations = new HashSet<>();
        private long duration = TimeUnit.SECONDS.toMillis(1);

        public Builder addDuration(long time, TimeUnit unit) {
            statsDurations.add(unit.toMillis(time));
            return this;
        }

        public SimpleDurationStats<SimpleCounter> build() {
            return build(SimpleCounter::new);
        }

        public <V extends Duration> SimpleDurationStats<V> build(Function<Long, V> counterFactory) {
            return build(counterFactory, DurationStatsUtils::merge);
        }

        public <V extends Duration> SimpleDurationStats<V> build(Function<Long, V> counterFactory,
                BinaryOperator<V> counterMerger) {
            Preconditions.checkNotNull(counterFactory);
            Preconditions.checkNotNull(counterMerger);
            ensure();
            return new SimpleDurationStats<>(statsDurations, duration, counterFactory,
                    counterMerger);
        }

        private void ensure() {
            if (statsDurations.isEmpty()) {
                statsDurations.add(SimpleDurationFormatter.HOUR);
                statsDurations.add(SimpleDurationFormatter.MINUTE);
                statsDurations.add(SimpleDurationFormatter.TEN_SECOND);
            }
        }
    }

    public static final Builder newBuilder() {
        return new Builder();
    }
}
