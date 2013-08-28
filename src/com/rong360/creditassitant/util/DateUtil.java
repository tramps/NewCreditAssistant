package com.rong360.creditassitant.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

/**
 * provide some data formats and util functions.
 */
public class DateUtil {
    public static final String DateFormatString = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DateFormatString2 = "yyyy-MM-dd HH:mm:ss";
    public static final String DateFormatString3 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static SimpleDateFormat yyyyMMddHHmmssS = new SimpleDateFormat(
	    DateFormatString);
    public static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
	    DateFormatString2);
    public static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat(
	    DateFormatString3);
    public static SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm");

    public static SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat(
	    "yyyy-MM-dd");
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat MMdd = new SimpleDateFormat("MM-dd");
    public static SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat MMddHHmm = new SimpleDateFormat(
	    "MM-dd HH:mm");

    public static Calendar today = Calendar.getInstance();

    private static final String SUFFIX_JUST_NOW = "刚刚";
    private static final String SUFFIX_MINUTE = "分钟前";
    private static final String SUFFIX_HOUR = "小时前";
    private static final String YESTERDAY = "昨天";
    private static final String TOMMORROW = "明天";
    private static final String TODAY = "今天";

    private static final String DOUBLE_ZERO = "00";
    private static final String SINGLE_ZERO = "0";
    private static final String COMMA = ":";

    private static final String[] WeekDay = { "周一", "周二", "周三", "周四",
	    "周五", "周六", "周日" };

    public static String getWeekDay(Date date) {
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	return WeekDay[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static boolean isSameDay(Date d1, Date d2) {
	return d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth()
		&& d1.getDate() == d2.getDate();
    }

    public static boolean isSameYear(Date d1, Date d2) {
	return d1.getYear() == d2.getYear();
    }

    public static boolean isDayToday(Date day) {
	Date today = new Date();
	return day.getYear() == today.getYear()
		&& day.getMonth() == today.getMonth()
		&& day.getDay() == today.getDay();
    }

    public static boolean isDayTomorrow(Date day) {
	Calendar today = Calendar.getInstance();
	today.add(Calendar.DAY_OF_MONTH, 1);
	return isSameDay(today.getTime(), day);
    }

    public static boolean isSameDay(Calendar baseDate, Calendar date) {
	if (date == null || baseDate == null) {
	    return false;
	}

	return (baseDate.get(Calendar.YEAR) == date.get(Calendar.YEAR)
		&& baseDate.get(Calendar.MONTH) == date.get(Calendar.MONTH) && baseDate
		    .get(Calendar.DAY_OF_MONTH) == date
		.get(Calendar.DAY_OF_MONTH));
    }

    // TODO
    public static boolean isNextDay(Calendar baseDate, Calendar date) {
	return (baseDate.get(Calendar.DAY_OF_YEAR) == date
		.get(Calendar.DAY_OF_YEAR) - 1);
    }

    public static boolean isYesterday(Calendar baseDate, Calendar date) {
	return (baseDate.get(Calendar.DAY_OF_YEAR) == date
		.get(Calendar.DAY_OF_YEAR) + 1);
    }

    public static boolean isLaterDay(Calendar baseDate, Calendar date) {
	return (baseDate.get(Calendar.DAY_OF_YEAR) == date
		.get(Calendar.DAY_OF_YEAR) - 2);
    }

    public static boolean isOtherDay(Calendar baseDate, Calendar date) {
	return (baseDate.get(Calendar.DAY_OF_YEAR) <= date
		.get(Calendar.DAY_OF_YEAR) - 3);
    }

    public static boolean isSameWeek(Calendar baseDate, Calendar date) {
	return (baseDate.get(Calendar.WEEK_OF_YEAR) == date
		.get(Calendar.WEEK_OF_YEAR));
    }

    public static String getDisplayTime(long time) {
	Calendar callTime = Calendar.getInstance();
	callTime.setTimeInMillis(time);

	Calendar now = Calendar.getInstance();
	now.setTimeInMillis(System.currentTimeMillis());

	StringBuilder sb = new StringBuilder();
	if (isSameDay(now, callTime)) {
	    long diff = now.getTimeInMillis() - callTime.getTimeInMillis();
	    long seconds = diff / 1000;
	    long hour = seconds / 3600;
	    long minute = (seconds - hour * 3600) / 60;
	    if (hour != 0) {
		sb.append(hour);
		sb.append(SUFFIX_HOUR);
	    } else if (minute != 0) {
		sb.append(minute);
		sb.append(SUFFIX_MINUTE);
	    } else {
		sb.append(SUFFIX_JUST_NOW);
	    }
	} else if (isYesterday(now, callTime)) {
	    sb.append(YESTERDAY);
	} else if (isNextDay(now, callTime)) {
	    sb.append(TOMMORROW);
	} else if (isSameWeek(now, callTime)) {
	    sb.append(getWeekDay(callTime.getTime()));
	} else {
	    sb.append(yyyy_MM_dd.format(callTime.getTime()));
	}

	return sb.toString();
    }

    public static String getDisplayTimeForNotification(long time) {
	Calendar callTime = Calendar.getInstance();
	callTime.setTimeInMillis(time);

	Calendar now = Calendar.getInstance();
	now.setTimeInMillis(System.currentTimeMillis());

	StringBuilder sb = new StringBuilder();
	sb.append(yyyy_MM_dd.format(callTime.getTime()));
	sb.append(" ");
	if (isSameDay(now, callTime)) {
	    sb.append(TODAY);
	} else if (isYesterday(now, callTime)) {
	    sb.append(YESTERDAY);
	} else if (isNextDay(now, callTime)) {
	    sb.append(TOMMORROW);
	} else if (isSameWeek(now, callTime)) {
	    sb.append(getWeekDay(callTime.getTime()));
	}
	return sb.toString();
    }

    public static String getDisplayTimeForDetail(long time) {
	Calendar callTime = Calendar.getInstance();
	callTime.setTimeInMillis(time);

	Calendar now = Calendar.getInstance();
	now.setTimeInMillis(System.currentTimeMillis());

	StringBuilder sb = new StringBuilder();
	if (isSameDay(now, callTime)) {
	    long diff = now.getTimeInMillis() - callTime.getTimeInMillis();
	    long seconds = diff / 1000;
	    long hour = seconds / 3600;
	    long minute = (seconds - hour * 3600) / 60;
	    if (hour != 0) {
		sb.append(hour);
		sb.append(SUFFIX_HOUR);
	    } else if (minute != 0) {
		sb.append(minute);
		sb.append(SUFFIX_MINUTE);
	    } else {
		sb.append(SUFFIX_JUST_NOW);
	    }
	} else if (isYesterday(now, callTime)) {
	    sb.append(YESTERDAY);
	} else if (isNextDay(now, callTime)) {
	    sb.append(TOMMORROW);
	} else if (isSameWeek(now, callTime)) {
	    sb.append(getWeekDay(callTime.getTime()));
	} else {
	    sb.append(yyyy_MM_dd.format(callTime.getTime()));
	}

	if (!isSameDay(callTime, now)) {
	    sb.append(" ");
	    sb.append(callTime.get(Calendar.HOUR_OF_DAY));
	    sb.append(":");
	    sb.append(callTime.get(Calendar.MINUTE));
	}

	return sb.toString();
    }
    
    public static String getExactTime(long time) {
	Calendar calc = Calendar.getInstance();
	calc.setTimeInMillis(time);
	StringBuilder sb = new StringBuilder();
	sb.append(calc.get(Calendar.HOUR_OF_DAY));
	sb.append(":");
	sb.append(calc.get(Calendar.MINUTE));
	
	return sb.toString();
    }

    public static CharSequence getDisplayForDuration(long duration) {
	long hour = duration / 3600;
	long minute = (duration - hour * 3600) / 60;
	long second = duration % 60;

	StringBuilder sb = new StringBuilder();
	if (hour != 0) {
	    if (hour >= 10) {
		sb.append(hour);
	    } else {
		sb.append(SINGLE_ZERO);
		sb.append(hour);
	    }
	} else {
	    sb.append(DOUBLE_ZERO);
	}
	sb.append(COMMA);

	if (minute != 0) {
	    if (minute >= 10) {
		sb.append(minute);
	    } else {
		sb.append(SINGLE_ZERO);
		sb.append(minute);
	    }
	} else {
	    sb.append(DOUBLE_ZERO);
	}
	sb.append(COMMA);

	if (second != 0) {
	    if (second >= 10) {
		sb.append(second);
	    } else {
		sb.append(SINGLE_ZERO);
		sb.append(second);
	    }
	} else {
	    sb.append(DOUBLE_ZERO);
	}

	return sb.toString();
    }

}
