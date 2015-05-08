/**
 * 
 */
package com.github.phantomthief.stats;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author w.vela
 */
public class SimpleDurationFormatter {

    private static final long SECOND = TimeUnit.SECONDS.toMillis(1);
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1);

    public static final Function<Long, String> of() {
        return l -> {
            if (l == HOUR) {
                return "hour";
            } else if (l == MINUTE) {
                return "minute";
            } else if (l == SECOND) {
                return "second";
            } else {
                return l + "";
            }
        };
    }
}
