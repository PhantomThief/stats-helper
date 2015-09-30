/**
 * 
 */
package com.github.phantomthief.stats.n.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.github.phantomthief.stats.n.DurationStats;
import com.github.phantomthief.stats.n.MultiDurationStats;
import com.github.phantomthief.stats.n.counter.Duration;
import com.github.phantomthief.stats.n.counter.SimpleCounter;
import com.github.phantomthief.stats.n.util.DurationStatsUtils;
import com.github.phantomthief.stats.n.util.SharedStatsScheduledExecutorHolder;
import com.github.phantomthief.stats.n.util.SimpleDurationFormatter;

/**
 * @author w.vela
 */
public class SimpleDurationStats<V extends Duration> implements DurationStats<V>, AutoCloseable {

    private static Logger logger = getLogger(SimpleDurationStats.class);

    private static final long SECOND = SECONDS.toMillis(1);
    private static final long MINUTE = MINUTES.toMillis(1);
    private static final long MERGE_THRESHOLD = MINUTES.toMillis(2);

    private final Map<Long, V> counters = new ConcurrentHashMap<>();
    private final Set<Long> statsDurations;
    private final Function<Long, V> counterFactory;
    private final BinaryOperator<V> counterMerger;
    private final ScheduledFuture<?> cleanupScheduledFuture;

    private SimpleDurationStats(Set<Long> statsDurations, Function<Long, V> counterFactory,
            BinaryOperator<V> counterMerger) {
        long maxTimePeriod = statsDurations.stream().max(Comparator.naturalOrder()).get();
        this.statsDurations = statsDurations;
        this.counterFactory = counterFactory;
        this.counterMerger = counterMerger;
        this.cleanupScheduledFuture = SharedStatsScheduledExecutorHolder.getInstance()
                .scheduleWithFixedDelay(() -> {
                    long now = System.currentTimeMillis();
                    Iterator<Entry<Long, V>> iterator = counters.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<Long, V> entry = iterator.next();
                        if (now - entry.getKey() > maxTimePeriod) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("remove expired counter:{}", entry);
                            }
                            iterator.remove();
                        }
                        if (entry.getValue().duration() == SECOND
                                && now - entry.getKey() > MERGE_THRESHOLD) {
                            long mergedKey = entry.getKey() / MINUTE * MINUTE;
                            counters.merge(mergedKey, entry.getValue(), counterMerger);
                            iterator.remove();
                            if (logger.isDebugEnabled()) {
                                logger.debug("merge counter:{}, merge to:{}->{}", entry, mergedKey,
                                        counters.get(mergedKey));
                            }
                        }
                    }
                } , 1, 1, MINUTES);
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.stats.n.DurationStats#stat(java.util.function.Consumer)
     */
    @Override
    public void stat(Consumer<V> statsFunction) {
        try {
            long timePoint = System.currentTimeMillis() / SECOND * SECOND;
            V counter = counters.computeIfAbsent(timePoint, t -> counterFactory.apply(SECOND));
            statsFunction.accept(counter);
        } catch (Throwable e) {
            logger.error("Ops.", e);
        }
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
    public void close() {
        cleanupScheduledFuture.cancel(false);
    }

    public static final class SimpleMultiDurationStats<K, V extends Duration>
                                                      implements MultiDurationStats<K, V> {

        private final ConcurrentMap<K, DurationStats<V>> map = new ConcurrentHashMap<>();
        private final Supplier<SimpleDurationStats<V>> statsFactory;

        /**
         * @param statsFactory
         */
        private SimpleMultiDurationStats(Supplier<SimpleDurationStats<V>> statsFactory) {
            this.statsFactory = statsFactory;
        }

        /* (non-Javadoc)
         * @see com.github.phantomthief.stats.n.MultiDurationStats#stat(java.lang.Object, java.util.function.Consumer)
         */
        @Override
        public void stat(K key, Consumer<V> statsFunction) {
            map.computeIfAbsent(key, k -> statsFactory.get()).stat(statsFunction);
        }

        /* (non-Javadoc)
         * @see com.github.phantomthief.stats.n.MultiDurationStats#getStats()
         */
        @Override
        public Map<K, Map<Long, V>> getStats() {
            return map.entrySet().stream()
                    .collect(toMap(Entry::getKey, e -> e.getValue().getStats()));
        }

    }

    public static final class Builder {

        private final Set<Long> statsDurations = new HashSet<>();

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
            checkNotNull(counterFactory);
            checkNotNull(counterMerger);
            ensure();
            return new SimpleDurationStats<>(statsDurations, counterFactory, counterMerger);
        }

        public <K> SimpleMultiDurationStats<K, SimpleCounter> buildMulti() {
            return buildMulti(SimpleCounter::new);
        }

        public <K, V extends Duration> SimpleMultiDurationStats<K, V>
                buildMulti(Function<Long, V> counterFactory) {
            return buildMulti(counterFactory, DurationStatsUtils::merge);
        }

        public <K, V extends Duration> SimpleMultiDurationStats<K, V>
                buildMulti(Function<Long, V> counterFactory, BinaryOperator<V> counterMerger) {
            return new SimpleMultiDurationStats<>(() -> build(counterFactory, counterMerger));
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
