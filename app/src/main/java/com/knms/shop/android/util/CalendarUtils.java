package com.knms.shop.android.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** 获取时间工具 */
public class CalendarUtils {

	/** 补0 */
	private static String start = "";
	/**
	 * 获取月份数组
	 * @return
	 */
	public static int[] getMonths(){
		return getMonths(1);
	}
	public static int[] getMonths(int min){
		if(min < 1)
			min = 1;
		if(min > 12)
			min = min % 12;
		int months[] = new int[13 - min];
		for (int i = min; i < months.length; i++) {
			months[i-min] = i;
		}
		return months;
	}
	public static List<String> getMothsList(){
		return getMothsList(1);
	}
	public static List<String> getMothsList(int min){
		if(min < 1)
			min = 1;
		if(min > 12)
			min = min % 12;
		List<String> month = new ArrayList<String>();
		for (int i = min; i < 13; i++) {
			if (i < 10) {
				month.add(start+i);
			}else{
				month.add(String.valueOf(i));
			}
		}
		return month;
	}
	/**
	 * 获取月份数组
	 * @return
	 */
	public static String[] getMonthString(int min){
		if(min < 1)
			min = 1;
		if(min > 12)
			min = min % 12;
		String month[] = new String[13 - min];
		List<String> m = getMothsList(min);
		for (int i = 0; i < m.size(); i++) {
			month[i] = m.get(i);
		}
		return month;
	}
	public static String[] getMonthString(){
		return getMonthString(1);
	}
	/**
	 * 获取小时数组
	 * @return
	 */
	public static String[] getHours(int min){
		if(min < 0){
			min = 0;
		}
		if(min > 23)
			min = min % 24;
		int len = 24-min;
		String hours[] = new String[len];
		for (int i = min; i < 24; i++) {
			if(i<10)
				hours[i] = start+i;
			else
				hours[i] = ""+i;
		}
		return hours;
	}
	/**
	 * 将字符串改成long性
	 * @param beginDate
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static long dateToLong(String beginDate,String format) {
		if (TextUtils.isEmpty(beginDate))
			return 0;
		if (TextUtils.isEmpty(format))
			format = "yyyy-MM-dd HH:mm:ss";
		long diff=0;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			Date d1 = df.parse(beginDate);
			diff = d1.getTime() ;
			return diff;
		} catch (ParseException e) {

			e.printStackTrace();
		}

		return diff;
	}
	/**
	 * 获取小时数组
	 * @return
	 */
	public static String[] getHours(){
		return getHours(0);
	}
	/**
	 * 获取小时List
	 * @return
	 */
	public static List<String> getHoursList(int min){
		if(min < 0){
			min = 0;
		}
		if(min > 23)
			min = min % 24;
		List<String> hours = new ArrayList<String>();
		for (int i = min; i < 24; i++) {
			if(i<10)
				hours.add(start+i);
			else
				hours.add(""+i);
		}
		return hours;
	}
	/**
	 * 获取小时List
	 * @return
	 */
	public static List<String> getHoursList(){
		return getHoursList(0);
	}
	/**
	 * 获取分钟和秒
	 * @return
	 */
	public static String[] getMinteOrSec(){
		String minOrSec[] = new String[60];
		for (int i = 0; i < 60; i++) {
			if(i<10)
				minOrSec[i] = start+i;
			else
				minOrSec[i] = ""+i;
		}
		return minOrSec;
	}
	public static List<String> getMinteOrSecList(){
		return getMinteOrSecList(0);
	}
	/** 获取分钟时间段 下表从0开始*/
	public static List<String> getMinteOrSecList(int min){
		if(min < 0){
			min = 0;
		}
		if(min > 59)
			min = min % 60;
		List<String> minOrSec = new ArrayList<String>();
		for (int i = min; i < 60; i++) {
			if(i<10)
				minOrSec.add(start+i);
			else
				minOrSec.add(""+i);
		}
		return minOrSec;
	}
	/**
	 * 获取 时间数组的位置
	 * @param times
	 * @param time
	 * @return
	 */
	public static int getCheckedItem(String[] times, int time){
		int toint[] =  StringToInt(times);
		for (int i = 0; i < toint.length; i++) {
			if(toint[i] == time)
				return i;
		}
		return 0;
	}
	/**
	 * Sring[] 转换int[]
	 * @param time
	 * @return
	 */
	public static int[] StringToInt(String[] time){
		int toint[] = new int[time.length];
		for (int i = 0; i < time.length; i++) {
			toint[i] = Integer.parseInt(time[i]);
		}
		return toint;
	}
	/**
	 * int[]转换String[]
	 * @param time
	 * @return
	 */
	public static String[] intToString(int[] time){
		String tostring[] = new String[time.length];
		for (int i = 0; i < time.length; i++) {
			tostring[i] = String.valueOf(time[i]);
		}
		return tostring;
	}
	/**
	 * 获取年份数组
	 * @param c
	 * @param newYear
	 * @return
	 */
	public static int[] getYears(Calendar c, int newYear){
		if(null == c)
			c = Calendar.getInstance();
		int now = getyear(c);
		int items = now - newYear;
		if(items == 0)
			return new int[]{getyear(c)};
		else if(items < 0)
			c.set(Calendar.YEAR, newYear);
		int[] years = new int[Math.abs(items)];
		years[0] = getyear(c);
		for (int i = 0; i < years.length; i++) {
			c.add(Calendar.YEAR, 1);
			years[i] = getyear(c);
		}
		c.set(Calendar.YEAR, now);
		return years;
	}
	/**
	 * 获取年份区间
	 * @param c
	 * @param minYear
	 * @param maxYear
	 * @return
	 */
	public static int[] getYears(Calendar c, int minYear, int maxYear){
		return StringToInt(getYearsString(c, minYear, maxYear));
	}
	public static String[] getYearsString(Calendar c, int minYear, int maxYear){
		if(null == c)
			c = Calendar.getInstance();
		int now = getyear(c);
		int min = minYear;
		if(minYear > maxYear){
			min = minYear ;
			minYear = maxYear;
			maxYear = min ;
		}else if(minYear == maxYear){
			c.set(Calendar.YEAR, minYear);
			String[] y = new String[]{String.valueOf(getyear(c))};
			c.set(Calendar.YEAR, now);
			return y;
		}
		c.set(Calendar.YEAR, minYear);
		String[] years = new String[Math.abs(maxYear - minYear)];
		years[0] = String.valueOf(getyear(c));
		for (int i = 0; i < years.length; i++) {
			c.add(Calendar.YEAR, 1);
			years[i] = String.valueOf(getyear(c));
		}
		c.set(Calendar.YEAR, now);
		return years;
	}
	public static List<String> getYearsList(Calendar c, int minYear, int maxYear){
		if(null == c)
			c = Calendar.getInstance();
		int now = getyear(c);
		int min = minYear;
		if(minYear > maxYear){
			min = minYear ;
			minYear = maxYear;
			maxYear = min ;
		}else if(minYear == maxYear){
			c.set(Calendar.YEAR, minYear);
			List<String> y = new ArrayList<String>();
			y.add(String.valueOf(getyear(c)));
			c.set(Calendar.YEAR, now);
			return y;
		}
		c.set(Calendar.YEAR, minYear);
		List<String> years = new ArrayList<String>();
		years.add(String.valueOf(getyear(c)));
		int size = Math.abs(maxYear - minYear);
		for (int i = 0; i < size; i++) {
			c.add(Calendar.YEAR, 1);
			years.add(String.valueOf(getyear(c)));
		}
		c.set(Calendar.YEAR, now);
		return years;
	}
	/**
	 * 获取一月的天数组
	 * @param c
	 * @return
	 */
	public static String[] getDays(Calendar c){
		return getDays(c, 0);
	}
	/** 获取一月中指定某天以后的数据 */
	public static String[] getDays(Calendar c, int min){
		if (min < 1) {
			min = 1;
		}
		if(null == c)
			c = Calendar.getInstance();
		int max = getdaysOfmonth(c);
		String[] days = new String[max - min+1];
		for (int i = min; i <= max; i++){
			if (i < 10) {
				days[i-min] = start+i;
			}else{
				days[i-min] = String.valueOf(i);
			}
		}
		return days;
	}
	/** 获取一月的天数据 */
	public static List<String> getDaysList(Calendar c){
		return getDaysList(c, 0);
	}
	/** 获取一月中指定某天以后的数据 */
	public static List<String> getDaysList(Calendar c, int min){
		List<String> days = new ArrayList<String>();
		String days_string[] = getDays(c,min);
		for (int i = 0; i < days_string.length; i++) {
			days.add(days_string[i]);
		}
		return days;
	}
	/** 获取某年某月的天数 */
	public static List<String> getDaysList(int year, int mon){
		Calendar c = Calendar.getInstance();
		int nowY = getyear(c);
		int nowM = getMonth(c) - 1;
		c.set(Calendar.YEAR,year);
		c.set(Calendar.MONTH,mon-1);
		List<String> data = getDaysList(c);
		c.set(Calendar.YEAR,nowY);
		c.set(Calendar.MONTH,nowM);
		return data;
	}
	/**
	 * 获取所在周的第一天
	 * @param c
	 * @param day
	 * @return
	 */
	public static int getFirstdayOfWeek(Calendar c, int day){
		if(null == c)
			c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_YEAR,1-day_of_week);
		int theday = c.get(Calendar.DATE);
		c.add(Calendar.DAY_OF_YEAR, day_of_week);
		return theday;
	}
	/**
	 * 获取所在周的最后天
	 * @param c
	 * @param day
	 * @return
	 */
	public static int getLastDatOfWeek(Calendar c, int day){
		if(null == c)
			c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_YEAR,7-day_of_week);
		int theday = c.get(Calendar.DATE);
		c.add(Calendar.DAY_OF_YEAR, day_of_week - 7);
		return theday;
	}

	/** 获取昨天的时间 */
	public static String getYesterday(String format){
		return format(getYesterday(),format);
	}
	/** 获取昨天的时间 */
	public static Date getYesterday(){
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,  -1);
		return cal.getTime();
	}
	/** 获取今天的时间 */
	public static String getToday(String format){
		return format(getToday(),format);
	}
	/** 获取今天的时间 */
	public static Date getToday(){
		return Calendar.getInstance().getTime();
	}
	public static String format(Date date){
		return format(date,null);
	}
	/** Date 转换 时间字符串 */
	public static String format(Date date,String format){
		if (null == date) return "";
		if (TextUtils.isEmpty(format)) format = "yyyy-MM-dd";
		return new SimpleDateFormat(format).format(date);
	}
	public static Date format(String date){
		return format(date,null);
	}
	/** 时间字符串转换 Date*/
	public static Date format(String date,String format){
		if (TextUtils.isEmpty(date)) return null;
		if (TextUtils.isEmpty(format)) format = "yyyy-MM-dd";
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {}
		return null;
	}

	/** 比较时间大小 <br/> -1 date1 < date2 <br/> 0 date1 == date2<br/> 1 date1 > date2*/
	public static int compare(String date1,String date2){
		Date dDate1 = format(date1);
		Date dDate2 = format(date2);
		return compare(dDate1,dDate2);
	}

	/** 比较时间大小 <br/> -1 date1 < date2 <br/> 0 date1 == date2<br/> 1 date1 > date2*/
	public static int compare(Date date1,Date date2){
		if (null == date1 && null == date2) return 0;
		if (null == date1 ) return -1;
		if (null == date2) return 1;
		if (date1.getTime() < date2.getTime()){
			return -1;
		}else if (date1.getTime() > date2.getTime()){
			return 1;
		}
		return 0;
	}
	/**
	 * 将字符串转为时间戳
	 * @param user_time
	 * @return
	 */
	public static String dateToTimestamp(String user_time,String format) {
		if (TextUtils.isEmpty(user_time))
			return "";
		if (TextUtils.isEmpty(format))
			format = "yyyy-MM-dd HH:mm:ss";
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}
	/**
	 * 自定义格式时间戳转换
	 *
	 * @param beginDate
	 * @return
	 */
	public static String timestampToDate(String beginDate,String format) {
		if(TextUtils.isEmpty(beginDate)){
			return "";
		}
		if (TextUtils.isEmpty(format))
			format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String sd = sdf.format(new Date(Long.parseLong(beginDate)));
		return sd;
	}

	/**  时间处理<br/>当为(今天\昨天)时返回 (今天|昨天) hh:mm else yyyy-mm-dd */
	public static String getDataCut(String data){
		if (TextUtils.isEmpty(data))
			return "";
		String cData = getDataFormat(data);
		String ch =  data.contains(" ")? data.substring(data.indexOf(" ")) : "";
		ch = getDataFormat(ch,"HH:mm");
		Date old = format(cData);//DateFormat
		Date now = format(getToday(null));
		Date yesterday = format(getYesterday(null));
		if (0 == compare(old,now)){
			return "今天 "+ ch;
		}else if(0 == compare(old,yesterday)){
			return "昨天 "+ ch;
		}
		return cData;
	}
	/**  时间处理 获取时间 yyyy-MM-dd */
	public static String getDataFormat(String data){
		return getDataFormat(data,null);
	}
	/**  时间格式转换*/
	public static String getDataFormat(String data,String format){
		if (TextUtils.isEmpty(data))
			return "";
		if (TextUtils.isEmpty(format)) format = "yyyy-MM-dd";
		return format(format(data,format),format);
	}
	/**
	 * 获取年份
	 * @param c
	 * @return
	 */
	public static int getyear(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}
	
	public static int getMonth(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.MONTH) + 1;
	}
	public static int getDay(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH);
	}
	public static int getHour(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}
	public static int getMin(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.MINUTE);
	}
	public static int getSec(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.get(Calendar.SECOND);
	}
	public static int getdaysOfmonth(Calendar c){
		if(null == c)
			c = Calendar.getInstance();
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
