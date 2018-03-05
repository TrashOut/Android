/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.trashout.R;
import me.trashout.model.Constants;

public class DateTimeUtils {

    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
    public static final SimpleDateFormat SHORT_DATE_TIME_FORMAT = new SimpleDateFormat("d.M. HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat VERSION_DATE_FORMAT = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static final Map<Integer, Long> times = new LinkedHashMap<>();

    static {
        times.put(R.string.global_time_year, TimeUnit.DAYS.toMillis(365));
        times.put(R.string.global_time_month, TimeUnit.DAYS.toMillis(30));
        times.put(R.string.global_time_week, TimeUnit.DAYS.toMillis(7));
        times.put(R.string.global_day, TimeUnit.DAYS.toMillis(1));
        times.put(R.string.global_time_hour, TimeUnit.HOURS.toMillis(1));
        times.put(R.string.global_minutes, TimeUnit.MINUTES.toMillis(1));
        times.put(R.string.global_time_second, TimeUnit.SECONDS.toMillis(1));
    }

    public static String computeTimeString(Context context, long duration, int maxLevel, boolean maxOnly, boolean showAgo) {
        if(context == null) return "";

        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<Integer, Long> time : times.entrySet()) {
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0) {
                res.append(timeDelta > 1 ? timeDelta : "a")
                        .append(" ")
                        .append(context.getString(time.getKey()))
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel || (res.length() > 0 && maxOnly)) {
                break;
            }
        }
        if ("".equals(res.toString())) {
            if (showAgo)
                return context.getString(R.string.global_time_0secAgo);
            return context.getString(R.string.global_time_0sec);
        } else {
            res.setLength(res.length() - 2);
            if (showAgo) {
                res.append(" ").append(context.getString(R.string.global_time_ago));
            }
            return res.toString();
        }
    }

    public static String toRelative(Context context, long duration) {
        return computeTimeString(context, duration, times.size(), true, true);
    }

    public static String getDurationTimeString(Context context, long duration) {
        return computeTimeString(context, duration, times.size(), true, false);
    }

    public static String toRelative(Context context, Date start, Date end) {
        return toRelative(context, end.getTime() - start.getTime());
    }

    public static String toRelative(Context context, Date start, Date end, int level) {
        return computeTimeString(context, end.getTime() - start.getTime(), level, true, true);
    }

    public static String toRelativeNow(Context context, Date date) {
        return toRelative(context, new Date().getTime() - date.getTime());
    }

    public static String toRelativeNow(Context context, Date date, boolean maxonly) {
        return computeTimeString(context, new Date().getTime() - date.getTime(), times.size(), maxonly, true);
    }

    public static String toRelativeNow(Context context, Date date, int maxlevel, boolean maxonly) {
        return computeTimeString(context, new Date().getTime() - date.getTime(), maxlevel, maxonly, true);
    }

    public static String getDateBefore(Constants.LastUpdate lastUpdate) {
        long date = System.currentTimeMillis();
        switch (lastUpdate) {
            case TODAY:
                date -= TimeUnit.DAYS.toMillis(1);
                break;

            case LAST_WEEK:
                date -= TimeUnit.DAYS.toMillis(7);
                break;

            case LAST_MONTH:
                date -= TimeUnit.DAYS.toMillis(30);
                break;

            case LAST_YEAR:
                date -= TimeUnit.DAYS.toMillis(365);
                break;
        }

        Date daysBeforeDate = new Date(date);
        return TIMESTAMP_FORMAT.format(daysBeforeDate);
    }

    public static long getDifferenceInMinutes(Date dateFrom, Date dateTo) {
        long diffInMillisec = dateFrom.getTime() - dateTo.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
    }

    public static String getRoundedTimeAgo(Context context, Date date) {
        long time = new Date().getTime() - date.getTime();

        if (time <= TimeUnit.DAYS.toMillis(1)) {
            return context.getString(R.string.trash_lastUpdate_today);
        } else if (time <= TimeUnit.DAYS.toMillis(2)) {
            return context.getString(R.string.trash_lastUpdate_yesterday);
        } else if (time <= TimeUnit.DAYS.toMillis(7)) {
            return context.getString(R.string.trash_lastUpdate_thisWeek);
        } else if (time < TimeUnit.DAYS.toMillis(30)) {
            return context.getString(R.string.trash_lastUpdate_moreThanWeekAgo);
        } else if (time < TimeUnit.DAYS.toMillis(183)) {
            return context.getString(R.string.trash_lastUpdate_moreThanMonthAgo);
        } else if (time < TimeUnit.DAYS.toMillis(365)) {
            return context.getString(R.string.trash_lastUpdate_moreThanSixMonthAgo);
        } else {
            return context.getString(R.string.trash_lastUpdate_moreThanYearAgo);
        }
    }
}
