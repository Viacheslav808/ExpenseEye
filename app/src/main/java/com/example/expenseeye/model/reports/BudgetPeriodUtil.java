package com.example.expenseeye.model.reports;

import java.util.Calendar;

/**
 * Utility for computing budget period boundaries.
 * Keeps date math in one place so fragments and view models stay clean.
 */
public final class BudgetPeriodUtil {

    private BudgetPeriodUtil() {
        // no instances
    }

    /** A simple container for a start/end timestamp pair. */
    public static final class Period {
        public final long start;
        public final long end;
        public Period(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Returns the start/end timestamps for the current calendar month,
     * spanning 00:00:00 on day 1 to 23:59:59 on the last day.
     */
    public static Period currentMonth() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long end = cal.getTimeInMillis();

        return new Period(start, end);
    }
}