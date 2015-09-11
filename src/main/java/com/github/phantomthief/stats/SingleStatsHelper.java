/**
 * 
 */
package com.github.phantomthief.stats;

import static java.util.function.Function.identity;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author w.vela
 */
public class SingleStatsHelper<C> {

    private static final String DEFAULT_KEY = "s";
    private final StatsHelper<String, C> stats;

    /**
     * @param stats
     */
    private SingleStatsHelper(StatsHelper<String, C> stats) {
        this.stats = stats;
    }

    public void stats(Consumer<C> counterFunction) {
        stats.stats(DEFAULT_KEY, counterFunction);
    }

    public Map<Long, C> getStats() {
        return stats.getStats().get(DEFAULT_KEY);
    }

    public <V> Map<String, V> getFriendlyStats(Function<C, V> counterFormatter) {
        return stats.getFriendlyStats(identity(), counterFormatter).get(DEFAULT_KEY);
    }

    public <D, V> Map<D, V> getFriendlyStats( //
            Function<Long, D> durationFormatter, //
            Function<C, V> counterFormatter) {
        return stats.getFriendlyStats(identity(), durationFormatter, counterFormatter)
                .get(DEFAULT_KEY);
    }

    public static final class Builder<C> {

        private final com.github.phantomthief.stats.StatsHelper.Builder<C> builder = StatsHelper
                .newBuilder();

        public Builder<C> addDuration(long duration, TimeUnit timeUnit) {
            builder.addDuration(duration, timeUnit);
            return this;
        }

        public Builder<C> setCounterReset(UnaryOperator<C> resetter) {
            builder.setCounterReset(resetter);
            return this;
        }

        public Builder<C>
                setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
            builder.setScheduledExecutorService(scheduledExecutorService);
            return this;
        }

        public SingleStatsHelper<C> build() {
            return new SingleStatsHelper<>(builder.build());
        }
    }

    public static final <C> Builder<C> newBuilder() {
        return new Builder<>();
    }

}
