/**
 * 
 */
package com.github.phantomthief.stats.n.util;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 * @author w.vela
 */
public class SimpleDurationFormatter {

    public static final long TEN_SECOND = SECONDS.toMillis(10);
    public static final long MINUTE = MINUTES.toMillis(1);
    public static final long HOUR = HOURS.toMillis(1);

    public static String format(long l) {
        if (l == HOUR) {
            return "hour";
        } else if (l == MINUTE) {
            return "minute";
        } else if (l == TEN_SECOND) {
            return "10seconds";
        } else {
            return PeriodFormat.getDefault().print(new Period(l));
        }
    }
}
