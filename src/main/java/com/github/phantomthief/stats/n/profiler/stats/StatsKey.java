/**
 * 
 */
package com.github.phantomthief.stats.n.profiler.stats;

/**
 * @author w.vela
 */
public interface StatsKey<T extends Stats> {

    default String getValue() {
        if (this instanceof Enum) {
            Enum<?> enum1 = (Enum<?>) this;
            return enum1.name();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    Class<T> statsType();
}
