package com.tryand.common;

import java.util.Calendar;

/**
 * Created by skammila on 11/22/15.
 */
public class Utils {

    public static boolean isDueToday(long time) {
        long start = getStartOfDayInMillis();
        long end = getEndOfDayInMillis();
        if (time >= start && time < end) {
            return true;
        }

        return false;
    }

    public static long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDayInMillis() {
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis() + (24 * 60 * 60 * 1000);
    }
}
