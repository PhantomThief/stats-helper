/**
 * 
 */
package com.github.phantomthief.stats.n.profiler.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.phantomthief.collection.BufferTrigger;
import com.github.phantomthief.collection.impl.SimpleBufferTrigger;
import com.github.phantomthief.stats.n.profiler.stats.Stats;
import com.github.phantomthief.stats.n.profiler.stats.StatsDTO;
import com.github.phantomthief.stats.n.profiler.stats.StatsKey;
import com.github.phantomthief.util.TriConsumer;

/**
 * @author w.vela
 */
public final class BufferStatsProfilerHelper {

    private static org.slf4j.Logger logger = getLogger(BufferStatsProfilerHelper.class);

    private final TriConsumer<Long, StatsKey<?>, Stats> statsSender;

    private final BufferTrigger<StatsDTO<? extends StatsKey<?>, Stats>> buffer;

    /**
     * @param bufferSize
     * @param statsSender
     */
    public BufferStatsProfilerHelper(int bufferSize, long duration,
            TriConsumer<Long, StatsKey<?>, Stats> statsSender) {
        this.statsSender = statsSender;
        this.buffer = SimpleBufferTrigger
                .<StatsDTO<? extends StatsKey<?>, Stats>, Map<StatsKey<?>, Stats>> newGenericBuilder() //
                .maxBufferCount(bufferSize, this::reject) //
                .consumer(this::sendBuffer) //
                .setContainer(ConcurrentHashMap::new, this::addToBuffer) //
                .on(duration, MILLISECONDS, 1) //
                .build();
    }

    private void sendBuffer(Map<StatsKey<?>, Stats> buffer) {
        buffer.forEach((key, stats) -> statsSender.consume(System.currentTimeMillis(), key, stats));
    }

    private boolean addToBuffer(Map<StatsKey<?>, Stats> buffer,
            StatsDTO<? extends StatsKey<?>, Stats> wrapper) {
        Stats stats = wrapper.getStats();
        return stats == buffer.merge(wrapper.getKey(), (Stats) stats, Stats::merge);
    }

    private void reject(StatsDTO<? extends StatsKey<?>, ? extends Stats> wrapper) {
        logger.warn("stats profiler buffer is full, reject:{}", wrapper);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends StatsKey<E>, E extends Stats> void stats(T key, E stats) {
        buffer.enqueue(new StatsDTO(key, stats));
    }
}
