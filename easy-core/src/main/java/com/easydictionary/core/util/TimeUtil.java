package com.easydictionary.core.util;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @Date: 2019/3/20
 * @Auther: dwy
 */
public class TimeUtil {

    public class Unit {


        /**
         * 一秒对应的秒数
         */
        public static final int SECOND = 1;
        /**
         * 一分钟对应的秒数
         */
        public static final int MINUTE = 60 ;
        /**
         * 一天对应的秒数
         */
        public static final int DAY = 24 * 60 * 60 ;
        /**
         * 一小时对应的秒数
         */
        public static final int HOUR = 60 * 60;
        /**
         * 半小时对应的秒数
         */
        public static final int HALF_HOUR = 30 * 60;

    }

    /** 一秒对应的毫秒数 */
    public static final long SECOND_MS = 1000L;
    /** 一分钟对应的毫秒数 */
    public static final long MINUTE_MS = 60 * 1000L;
    /** 一天对应的毫秒数 */
    public static final long DAY_MS = 24 * 60 * 60 * 1000L;
    /** 一分钟对应的秒数 */
    public static final int MINUTE_SECOND = 60 * 60;
    /** 一天对应的秒数 */
    public static final int DAY_SECOND = 24 * MINUTE_SECOND;
    /** 一小时对应的秒数 */
    public static final int HOUR_SECOND = 60 * 60;
    /** 半小时对应的秒数 */
    public static final int HALF_HOUR_SECOND = 30 * 60;

    public static final String DATA_FORMAT1 = "yyyy-MM-dd HH:mm:ss";

    public static int getCurrentSecond() {
        return (int) (System.currentTimeMillis() / SECOND_MS);
    }

    public static int getCurrentMinute() {
        return (int) (System.currentTimeMillis() / MINUTE_MS);
    }

    public static int getCurrentDay() {
        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
        return d;
    }

    public static int getDay(long second) {
        return getDay(new Date(second * SECOND_MS));
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int d = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
        return d;
    }

    public static String getNowString() {
        SimpleDateFormat format = new SimpleDateFormat(DATA_FORMAT1);
        return format.format(new Date());
    }

    public static String getDateString(Date d) {
        SimpleDateFormat format = new SimpleDateFormat(DATA_FORMAT1);
        return format.format(d);
    }

    public static String getSecondString(int secondTime) {
        SimpleDateFormat format = new SimpleDateFormat(DATA_FORMAT1);
        return format.format(new Date(secondTime * 1000));
    }



    /**
     * 获取该天的凌晨时刻（秒）
     *
     * @param currentSecond
     * @return
     */
    public static int getTodayZone(Integer currentSecond) {
        Calendar calendar = Calendar.getInstance();
        if(currentSecond!=null){
            calendar.setTimeInMillis(currentSecond * 1000L);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取当的凌晨时刻（秒）
     * @return
     */
    public static int getTodayZone() {
        return getTodayZone(null);
    }

    /**
     * 获取下次指定时间点的时间戳秒数  比如下次 02:00:00或02:00  的时间戳
     * @return
     */
    public static int getNextSecond(String fixStr) {
        int second = getTodayZone();
        String[] strs =fixStr.split(":");
        switch (strs.length){
            case 3:
                second += Integer.parseInt(strs[2]);
            case 2:
                second += Integer.parseInt(strs[1]) * Unit.MINUTE;
            case 1:
                second += Integer.parseInt(strs[0]) * Unit.HOUR;
        }
        if(second < getCurrentSecond()){
            second += Unit.DAY;
        }

        return second;
    }


    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    public  static void main(String[] str){
        System.out.println(getNextSecond("02:30"));
    }
}
