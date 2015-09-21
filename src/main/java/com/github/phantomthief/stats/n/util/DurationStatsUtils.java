/**
 * 
 */
package com.github.phantomthief.stats.n.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import com.github.phantomthief.stats.n.counter.Duration;
import com.google.common.base.Throwables;

/**
 * @author w.vela
 */
public final class DurationStatsUtils {

    private DurationStatsUtils() {
        throw new UnsupportedOperationException();
    }

    public static final <V extends Duration> V merge(V o1, V o2) {
        if (o2 == null) {
            throw new IllegalStateException();
        }
        try {
            @SuppressWarnings("unchecked")
            V result = (V) o2.getClass().getConstructor(long.class).newInstance(0L);
            for (Field field : o2.getClass().getDeclaredFields()) {
                if (field.getType() == AtomicInteger.class) {
                    int value = doGet(field, o1, Number::intValue)
                            + doGet(field, o2, Number::intValue);
                    ((AtomicInteger) field.get(result)).set(value);
                } else if (field.getType() == AtomicLong.class) {
                    long value = doGet(field, o1, Number::longValue)
                            + doGet(field, o2, Number::longValue);
                    ((AtomicLong) field.get(result)).set(value);
                }
                result.setDuration(o1.duration() + o2.duration());
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * @param result
     * @param o1
     * @param o2
     * @param object
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private static final <V> V doGet(Field field, Object result, Function<Number, V> getter)
            throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Object object = field.get(result);
        return getter.apply((Number) object);
    }
}
