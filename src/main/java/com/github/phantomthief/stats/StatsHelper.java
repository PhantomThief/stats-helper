/**
 * 
 */
package com.github.phantomthief.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author w.vela
 */
public class StatsHelper<T, C> {

    private final Map<T, Map<Long, C>> statsMap = new ConcurrentHashMap<>();
    private final Set<Long> resetDuration;
    private final UnaryOperator<C> resetter;

    /**
     * @param scheduledExecutorService
     * @param resetter
     * @param resetDuration
     */
    private StatsHelper(ScheduledExecutorService scheduledExecutorService,
            UnaryOperator<C> resetter, Set<Long> resetDuration) {
        this.resetDuration = resetDuration;
        this.resetter = resetter;
        if (resetDuration != null) {
            resetDuration
                    .forEach(duration -> scheduledExecutorService.scheduleWithFixedDelay(() -> {
                        statsMap.values().forEach(map -> {
                            map.computeIfPresent(duration, (d, old) -> this.resetter.apply(old));
                        } );
                    } , duration, duration, TimeUnit.MILLISECONDS));
        }
    }

    public void stats(T name, Consumer<C> counterFunction) {
        Map<Long, C> thisMap = statsMap.computeIfAbsent(name, n -> new ConcurrentHashMap<>());
        for (Long duration : resetDuration) {
            counterFunction.accept(thisMap.computeIfAbsent(duration, n -> resetter.apply(null)));
        }
    }

    public Map<T, Map<Long, C>> getStats() {
        return statsMap;
    }

    public <K, D, V> Map<K, Map<D, V>> getFriendlyStats( //
            Function<T, K> nameFormatter, //
            Function<Long, D> durationFormatter, //
            Function<C, V> counterFormatter) {
        Map<K, Map<D, V>> result = new HashMap<>();
        for (Entry<T, Map<Long, C>> entry : statsMap.entrySet()) {
            Map<D, V> thisMap = new HashMap<>();
            for (Entry<Long, C> e : entry.getValue().entrySet()) {
                thisMap.put(durationFormatter.apply(e.getKey()),
                        counterFormatter.apply(e.getValue()));
            }
            result.put(nameFormatter.apply(entry.getKey()), thisMap);
        }
        return result;
    }

    public static final <C> Builder<C> newBuilder() {
        return new Builder<>();
    }

    public static final class Builder<C> {

        private final Set<Long> duration = new HashSet<>();
        private UnaryOperator<C> counterResetter;
        private ScheduledExecutorService scheduledExecutorService;

        public Builder<C> addDuration(long duration, TimeUnit timeUnit) {
            this.duration.add(timeUnit.toMillis(duration));
            return this;
        }

        public Builder<C> setCounterReset(UnaryOperator<C> resetter) {
            this.counterResetter = resetter;
            return this;
        }

        public Builder<C> setScheduledExecutorService(
                ScheduledExecutorService scheduledExecutorService) {
            this.scheduledExecutorService = scheduledExecutorService;
            return this;
        }

        public <T> StatsHelper<T, C> build() {
            ensure();
            return new StatsHelper<>(scheduledExecutorService, counterResetter, duration);
        }

        /**
         * 
         */
        private void ensure() {
            if (scheduledExecutorService == null) {
                scheduledExecutorService = Executors.newScheduledThreadPool(1);
            }
            if (counterResetter == null) {
                throw new IllegalArgumentException("no counter resetter found.");
            }
            if (duration.isEmpty()) {
                throw new IllegalArgumentException("duration is empty.");
            }
        }
    }

}