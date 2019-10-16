package com.baselibrary.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created By pq
 * on 2019/9/6
 * 日期工具类
 */
public class CalendarUtil {

    /**
     * 获取当前系统的年
     */
     public static int getYear(){
         Calendar a = Calendar.getInstance();
         return a.get(Calendar.YEAR);
     }
    /**
     * 获取当前系统的月
     */
    public static int getMonth(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.MONTH);
    }
    /**
     * 获取当前系统的日
     */
    public static int getDay(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 获取当前系统时
     */
    public static int getHour(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * 获取当前系统的分
     */
    public static int getMinute(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.MINUTE);
    }
    /**
     * 获取当前系统的秒
     */
    public static int getMillisecond(){
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.MILLISECOND);
    }
    /**
     * 获取当月的 天数
     */
    public static int getCurrentMonthDay() {

        Calendar a = Calendar.getInstance();

        a.set(Calendar.DATE, 1);

        a.roll(Calendar.DATE, -1);

        int maxDate = a.get(Calendar.DATE);

        return maxDate;

    }


    /**
     * 根据年 月 获取对应的月份 天数
     */

    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();

        a.set(Calendar.YEAR, year);

        a.set(Calendar.MONTH, month - 1);

        a.set(Calendar.DATE, 1);

        a.roll(Calendar.DATE, -1);

        int maxDate = a.get(Calendar.DATE);

        return maxDate;

    }


    /**
     * 根据日期 找到对应日期的 星期
     */

    public static String getDayOfWeekByDate(String date) {

        String dayOfweek = "-1";

        try {

            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

            Date myDate = myFormatter.parse(date);

            SimpleDateFormat formatter = new SimpleDateFormat("E");

            String str = formatter.format(myDate);

            dayOfweek = str;


        } catch (Exception e) {

            System.out.println("错误!");

        }

        return dayOfweek;

    }

    /**
     * 得到几天前的时间
     *  
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *  
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }



}
