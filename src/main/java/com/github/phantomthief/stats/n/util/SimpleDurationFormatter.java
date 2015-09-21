/**
 * 
 */
package com.github.phantomthief.stats.n.util;

import java.util.concurrent.TimeUnit;

/**
 * @author w.vela
 */
public class SimpleDurationFormatter {

    public static final long TEN_SECOND = TimeUnit.SECONDS.toMillis(10);
    public static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
    public static final long HOUR = TimeUnit.HOURS.toMillis(1);

    public static final String format(long l) {
        if (l == HOUR) {
            return "hour";
        } else if (l == MINUTE) {
            return "minute";
        } else if (l == TEN_SECOND) {
            return "10seconds";
        } else {
            return l + "";
        }
    }
}
