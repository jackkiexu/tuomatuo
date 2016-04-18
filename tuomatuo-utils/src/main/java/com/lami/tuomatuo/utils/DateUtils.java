package com.lami.tuomatuo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {
	
	private static SimpleDateFormat formatter;
	
	// 将本时段的 毫秒，秒，分  都为 0
	public static Date getOnlyHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		return c.getTime();
	}
	
	// 将本时段的 毫秒，秒，分，时  都为 0
	public static Date getOnlyDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		return c.getTime();
	}


	public static String getGMTDate(){
		Date d=new Date();
		DateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(d);
	}

	public static Date getOffsetDate(Date date, int offset) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, offset);
		return c.getTime();
	}

	// 得到前/后几个月的同一时间点
	public static Date getOffsetMonth(Date date, int offset) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, offset);
		return c.getTime();
	}

	
	// 得到昨天的同一时间点
	public static Date getPrevDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}
	
	// 小时的偏差
	public static Date getOffsetHour(Date date, int mount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, mount);
		return c.getTime();
	}
	
	// 天的偏差
	public static Date getOffsetDay(Date date, int mount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, mount);
		return c.getTime();
	}
	
	// 得到明天的同一时间点
	public static Date getNextDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	// 得到今天的开始时间
	public static Date getDateBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		return c.getTime();
	}
	
	// 得到这个月的开始时间
	public static Date getMonthBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	// 得到今年的开始时间
	public static Date getYearBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		return c.getTime();
	}
	
	// 得到今天的最后一秒时间
	public static Date getDateEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.HOUR_OF_DAY, 23);
		return c.getTime();
	}

	/**
	 * 功能：获取本周的最后一天23:59:59
	 * */
	public static Date getLastDayOfCurrWeek() {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.HOUR_OF_DAY, 23);
		return c.getTime();
	}

	// 获取时间 ,mount 为小时漂移量 
	public static Date getBeforeHourByCurrentTime(Date now,int mount) {
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) +mount);
		return c.getTime();
	}
	
	// 得到下n分钟的时间
	public static Date getNextMinutesTime(Date time, int mount){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) +mount);
		return c.getTime();
	}

	

	// 得到两个日期的 minutes 之差
	public static synchronized int getMinutes(Date d1,Date d2){
		long diff = d2.getTime() - d1.getTime();   
		double days = Double.valueOf(diff) / (1000 * 60);   
		int result=(int)days;
		double in=Double.valueOf(result);
		if(days>in)
			return ++result;
		else
			return result;
	}
	
	//  将 Date 解析为 String 类型
	public static String shortDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(aDate);
	}

	public static String shortMailDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(aDate);
	}

	public static String mailDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyyMMddHHmm");
		return formatter.format(aDate);
	}
	
	public static String getYearAndMonth(Date date){
		if(date==null)
			return null;
		formatter = new SimpleDateFormat("yyyy-MM");
		return formatter.format(date);
	}

	public static String longLDAPDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(aDate);
	}

	public static String longDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(aDate);
	}

	public static String noSecondDate(Date aDate) {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatter.format(aDate);
	}

	public static String longDateGB(Date aDate) {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(aDate);
	}
	
	public static String LDAPDate(Date aDate) {
		return formatDate(aDate, "yyyyMMddHHmm'Z'");
	}
	
	// 有这个函数足够了
	public static String formatDate(Date aDate, String formatStr) {
		formatter = new SimpleDateFormat(formatStr);
		return formatter.format(aDate);
	}

	// yyyy-MM-dd HH:mm:ss
	public static Date parser(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}

	// 将 String 解析为 Date
	/**
	 * @param formatter   like yyyy-MM-dd HH:mm:ss
	 * @param strDate
	 * @return
	 */
	public static Date parser(String strDate, String formatter) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 *  通过传来 HH:mm:ss 得到今天时间
	 */
	public static Date getDateByTail(String tail){
		String nowDayString=DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		return DateUtils.parser(nowDayString+" "+tail,"yyyy-MM-dd HH:mm:ss");
	}
	
	/** 比较两个时间间距是否小于 10分钟
	 * @param first
	 * @param sec
	 * @return
	 */
	public static boolean compareDateDiff(Date first, Date sec){
		long firs = first.getTime();
		long se = sec.getTime();
		if(((firs > se) && (firs-se < 600000)) || ((se > firs) && (se - firs < 600000))){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 第一个时间是不是比第二个时间小于等于param毫秒
	 * @param first
	 * @param sec
	 * @param param
	 * @return
	 */
	public static boolean customCompareDateDiff(Date first, Date sec,int param){
		long firs = first.getTime();
		long se = sec.getTime();
		if((se > firs) && (se - firs <= param)){
			return true;
		}else{
			return false;
		}
	}
	
	
	//  !!!!
	public static Date addDay(Date myDate, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parser(formatDate(myDate, "yyyy-MM-dd"), "yyyy-MM-dd"));
		cal.add(Calendar.DAY_OF_MONTH, amount);
		return cal.getTime();
	}
	// 这不是和上面一样的
	public static Date addDate(Date myDate, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(myDate);
		cal.add(Calendar.DAY_OF_MONTH, amount);
		return cal.getTime();
	}
	
	
	

	public static Date removeDay(Date myDate, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parser(formatDate(myDate, "yyyy-MM-dd"), "yyyy-MM-dd"));
		cal.add(Calendar.DAY_OF_MONTH, -amount);
		return cal.getTime();
	}

	public static Date getFirstDay(Date date) {
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 1);
//		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
		return cale.getTime();
	}

	/*
	 * the mapping from jdk is : 1-Sun; 2-Mon;3-Tues; 4-Weds; 5-Thur;6-Fri;
	 * 7-Sat;
	 */
	public static int getWeekDay(Date myDate) { // 得到星期几
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(myDate);
		return cal.get(GregorianCalendar.DAY_OF_WEEK);
	}

	/*
	 * the mapping from vas2005 is : 1-Mon;2-Tues; 3-Weds; 4-Thur;5-Fri;
	 * 6-Sat;7-Sun;
	 */
	public static int getConvertWeekDay(Date myDate) {  // 得到星期几
		int day = getWeekDay(myDate);
		int result = day - 1;
		if (result == 0)
			result = 7;
		return result;
	}
	

	
	// 将 HHmmss 转为数字显示
	public static int getTimeFromDate(Date myDate) { 
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		int result = Integer.parseInt(sdf.format(myDate));
		return result;
	}
	
	// 得到 多少月之前  的 milliseconds
	public static long previousXMonth(long current, int count) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(new Date(current));
		aCalendar.getTime();
		aCalendar.add(Calendar.MONTH, -count);
		return aCalendar.getTime().getTime();
	}
	
	// 得到 多少秒之前  的 milliseconds 
	public static long nextXMonth(long current, int count) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(new Date(current));
		aCalendar.getTime();
		aCalendar.add(Calendar.MONTH, count);
		return aCalendar.getTime().getTime();
	}

	public static Date getDayAfterWorkingDay(Date date, int days) {
		int num = 0;
		int i = 0;
		while (true) {
			date = addDay(new Date(), i);
			num = getWorkingDays(new Date(), date);
			if (num == days)
				break;
			i++;
		}
		return date;
	}

	public static int getWorkingDays(Date startDate, Date endDate) {
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		formatDate(startDate, "yyyy-MM-dd");
		formatDate(endDate, "yyyy-MM-dd");
		cal_start.setTime(startDate);
		cal_end.setTime(endDate);
		return getWorkingDay(cal_start, cal_end);
	}

	public static int getDaysBetween(Calendar d1,
			Calendar d2) {
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(Calendar.DAY_OF_YEAR)
				- d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	private static int getWorkingDay(Calendar d1, Calendar d2) {
		int result = -1;
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int charge_start_date = 0;
		int charge_end_date = 0;

		int stmp;
		int etmp;
		stmp = 7 - d1.get(Calendar.DAY_OF_WEEK);
		etmp = 7 - d2.get(Calendar.DAY_OF_WEEK);
		if (stmp != 0 && stmp != 6) {
			charge_start_date = stmp - 1;
		}
		if (etmp != 0 && etmp != 6) {
			charge_end_date = etmp - 1;
		}
		result = (getDaysBetween(getNextMonday(d1), getNextMonday(d2)) / 7) * 5
				+ charge_start_date - charge_end_date;
		return result;
	}

	private static Calendar getNextMonday(Calendar date) {
		Calendar result = null;
		result = date;
		do {
			result = (Calendar) result.clone();
			result.add(Calendar.DATE, 1);
		} while (result.get(Calendar.DAY_OF_WEEK) != 2);
		return result;
	}

	public static Date getYesterday() {
		Calendar cale = Calendar.getInstance();
		cale.setTime(new Date());
		cale.add(Calendar.DATE, -1);
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}

	public static Date getYesterday(Date thisDate) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(thisDate);
		cale.add(Calendar.DATE, -1);
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}
	public static Date getDateBefore(Date thisDate,String format,int mount) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(thisDate);
		cale.add(Calendar.DATE, mount);
		return parser(formatDate(cale.getTime(), format),format);
	}

	public static Date getToday() {
		Calendar cale = Calendar.getInstance();
		cale.setTime(new Date());
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}

	public static Date getToday(Date thisDate) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(thisDate);
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}

	public static Date getTomorrow() {
		Calendar cale = Calendar.getInstance();
		cale.setTime(new Date());
		cale.add(Calendar.DATE, 1);
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}

	public static Date getTomorrow(Date thisDate) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(thisDate);
		cale.add(Calendar.DATE, 1);
		return parser(formatDate(cale.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
	}
	
	public static int getDaysBetween(String beginDate, String endDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			bDate = format.parse(beginDate);
			eDate = format.parse(endDate);
		} catch (ParseException e) {
			return -1;
		}
		Calendar d1 = new GregorianCalendar();
		d1.setTime(bDate);
		Calendar d2 = new GregorianCalendar();
		d2.setTime(eDate);
		int days = d2.get(6) - d1.get(6);
		int y2 = d2.get(1);
		if (d1.get(1) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(6);
				d1.add(1, 1);
			} while (d1.get(1) != y2);
		}
		return days;
	}

	public static String getDate() {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Date dd = new Date();
		return ft.format(dd);
	}

	public static String getDate(Date day) {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		return ft.format(day);
	}
	public static String getOnlyYear(Date day) {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy");
		return ft.format(day);
	}

	public static long getQuot(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}

	public static List<String> getBetweenTwoDayCalender(String startDay,
			String endDay) {
		List<String> list = new ArrayList<String>();
		if (startDay == null || startDay.equals("")) {
			return null;
		}
		if (endDay == null || endDay.equals("")) {
			return null;
		}
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startD = myFormatter.parse(startDay);
			Date endD = myFormatter.parse(endDay);
			long day = (endD.getTime() - startD.getTime())
					/ (24 * 60 * 60 * 1000);
			long dayMill = 24 * 60 * 60 * 1000;
			int d = Integer.parseInt(String.valueOf(day));
			String updateTime;
			long longTime;
			for (int i = 0; i <= d; i++) {
				longTime = startD.getTime() + dayMill * i;
				updateTime = myFormatter.format(new Date(longTime));
				list.add(updateTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String delayDay(String now,int days){
		return DateUtils.formatDate(DateUtils.addDate(DateUtils.parser(now, "yyyy-MM-dd HH:mm:ss"),days),"yyyy-MM-dd HH:mm:ss");
	}

	
	/**
	 * 判断是不是当月的第一天
	 */

	public static boolean isFirstDayForThisMonth(){
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(calendar.DAY_OF_MONTH);
		if(today ==1){
		 return true;
		}
		return false;
	}
	public static String  isThisMonth(){
		Calendar calendar = Calendar.getInstance();
		return String.valueOf(calendar.get(Calendar.MONTH) + 1);
		
	}


	public static Long getDifferHour(Date date){
		Date now = new Date();
		return (now.getTime() - date.getTime())/(1000 * 60 * 60);
	}

	public static void main(String[] args) {
		System.out.println(longDate(new Date()));
		System.out.println(longDate(getOffsetMonth(new Date(), -4)));

	}
}
