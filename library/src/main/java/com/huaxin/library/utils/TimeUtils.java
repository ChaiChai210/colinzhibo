package com.huaxin.library.utils;

import com.blankj.utilcode.util.StringUtils;

import java.util.Locale;

public class TimeUtils {
    private final static long minute = 60;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    public static String formatSeconds(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:"+unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 返回文字描述的日期
     *
     * @return
     */
    public static String getTimeFormatText(long time) {
        long now = System.currentTimeMillis();
        now = now / 1000;
        long diff = (int) now - time;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static int getSeconds(String time) {
        if (StringUtils.isEmpty(time)) {
            return 0;
        }
        //秒以后去掉
        String[] split = time.split("\\.");
        String[] seconds = split[0].split(":");
//        int length = seconds.length;

        int total = 0;
        total = Integer.parseInt(seconds[2]) + Integer.parseInt(seconds[1]) * 60 + Integer.parseInt(seconds[0]) * 60 * 60;
        return total;
    }

    public static String getDuration(String time) {
        String result = "";
        if (StringUtils.isEmpty(time) || time.equals("0")) {
            return "0:00";
        }
        //秒以后去掉
        String[] split = time.split("\\.");
        String[] seconds = split[0].split(":");
//        int length = seconds.length;
        if (seconds[0].equals("00")) {
            result = seconds[1] + ":" + seconds[2];
        } else {
            result = seconds[0] + ":" + seconds[1] + ":" + seconds[2];
        }
        return result;
    }
}
