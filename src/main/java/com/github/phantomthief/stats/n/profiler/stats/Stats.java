/**
 * 
 */
package com.github.phantomthief.stats.n.profiler.stats;

import static com.github.phantomthief.stats.n.profiler.util.StatsMergeUtils.annotationBasedMerge;

/**
 * @author w.vela
 */
public interface Stats {

    default Stats merge(Stats other) {
        return annotationBasedMerge(this, other);
    }
}
