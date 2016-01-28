/**
 * 
 */
package com.github.phantomthief.stats.n.profiler.stats;

/**
 * @author w.vela
 */
public class StatsDTO<T extends StatsKey<E>, E extends Stats> {

    private T key;
    private E stats;

    public StatsDTO() {
        // 无参构建喂狗(jackson)
    }

    public StatsDTO(T key, E stats) {
        this.key = key;
        this.stats = stats;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public E getStats() {
        return stats;
    }

    public void setStats(E stats) {
        this.stats = stats;
    }
}
