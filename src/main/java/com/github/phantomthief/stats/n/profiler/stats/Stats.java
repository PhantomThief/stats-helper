/**
 * 
 */
package com.github.phantomthief.stats.n.profiler.stats;

import com.github.phantomthief.stats.n.profiler.util.StatsMergeUtils;

/**
 * @author w.vela
 */
public interface Stats {

    default Stats merge(Stats other) {
        return StatsMergeUtils.annotationBasedMerge(this, other);
    }
}
