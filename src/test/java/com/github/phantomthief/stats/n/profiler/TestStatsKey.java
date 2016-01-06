/**
 * 
 */
package com.github.phantomthief.stats.n.profiler;

import com.github.phantomthief.stats.n.profiler.stats.StatsKey;

/**
 * @author w.vela
 */
public enum TestStatsKey implements StatsKey<SimpleStats> {
    biz1, //
    biz2, //
    ;

    /* (non-Javadoc)
     * @see com.github.phantomthief.stats.n.profiler.stats.StatsKey#statsType()
     */
    @Override
    public Class<SimpleStats> statsType() {
        return SimpleStats.class;
    }
}
