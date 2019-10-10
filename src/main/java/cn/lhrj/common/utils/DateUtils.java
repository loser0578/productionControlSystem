/**
 * Copyright 2015-2025 FLY的狐狸(email:jflyfox@sina.com qq:369191470).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package cn.lhrj.common.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.jfinal.kit.StrKit;

/**
 * 日期处理
 * 
 * 
 * 2014年5月5日 下午12:00:00
 * flyfox 330627517@qq.com
 */
public class DateUtils {
	
	public static final int SECOND = 1;
	public static final int MINUTE_SECOND = 60 * SECOND;
	public static final int HOUR_SECOND = 60 * MINUTE_SECOND;
	public static final int DAY_SECOND = 24 * HOUR_SECOND;
	public static final int WEEK_SECOND = 7 * DAY_SECOND;

	/** 日期格式：yyyy-MM-dd HH:mm:ss.SSS */
	public static final String YMD_HMSSS = "yyyy-MM-dd HH:mm:ss.SSS";
	/** 日期格式：yyyyMMddHHmmssSSS */
	public static final String YMDHMSSS = "yyyyMMddHHmmssSSS";
	/** 日期格式：yyyy-MM-dd HH:mm:ss */
	public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式：yyyy-MM-dd HH:mm */
	public static final String YMD_HM = "yyyy-MM-dd HH:mm";
	/** 日期格式：yyyyMMddHHmmss */
	public static final String YMDHMS = "yyyyMMddHHmmss";
	/** 日期格式：yyyy-MM-dd */
	public static final String YMD = "yyyy-MM-dd";
	/** 时间格式：HH:mm:ss */
	public static final String HMS = "HH:mm:ss";
	/**
	 * 时间间隔：日
	 */
	public final static int DATE_INTERVAL_DAY = 1;
	/**
	 * 时间间隔：周
	 */
	public final static int DATE_INTERVAL_WEEK = 2;
	/**
	 * 时间间隔：月
	 */
	public final static int DATE_INTERVAL_MONTH = 3;
	/**
	 * 时间间隔：年
	 */
	public final static int DATE_INTERVAL_YEAR = 4;
	/**
	 * 时间间隔：小时
	 */
	public final static int DATE_INTERVAL_HOUR = 5;
	/**
	 * 时间间隔：分钟
	 */
	public final static int DATE_INTERVAL_MINUTE = 6;
	/**
	 * 时间间隔：秒
	 */
	public final static int DATE_INTERVAL_SECOND = 7;
	/**
	 * 时间格式：年月日
	 */	
	/**
	 * 默认的日期格式 .
	 */
	public static final String DEFAULT_REGEX = "yyyy-MM-dd";
	/**
	 * 默认的日期格式 .
	 */
	public static final String DEFAULT_REGEX_YYYYMMDD = "yyyyMMdd";
	/**
	 * 默认的日期格式 .
	 */
	public static final String DEFAULT_REGEX_YYYY_MM_DD_HH_MIN_SS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 默认的DateFormat 实例
	 */
	private static final EPNDateFormat DEFAULT_FORMAT = new EPNDateFormat(DEFAULT_REGEX);
	/**
	 * 默认的DateFormat 实例
	 */
	private static final EPNDateFormat DEFAULT_FORMAT_YYYY_MM_DD_HH_MIN_SS = new EPNDateFormat(
			DEFAULT_REGEX_YYYY_MM_DD_HH_MIN_SS);
	/**
	 * 默认的DateFormat 实例
	 */
	private static final EPNDateFormat DEFAULT_FORMAT_YYYYMMDD = new EPNDateFormat(DEFAULT_REGEX_YYYYMMDD);
	private static Map<String, EPNDateFormat> formatMap = new HashMap<String, EPNDateFormat>();
	static {
		formatMap.put(DEFAULT_REGEX, DEFAULT_FORMAT);
		formatMap.put(DEFAULT_REGEX_YYYY_MM_DD_HH_MIN_SS, DEFAULT_FORMAT_YYYY_MM_DD_HH_MIN_SS);
		formatMap.put(DEFAULT_REGEX_YYYYMMDD, DEFAULT_FORMAT_YYYYMMDD);
	}



	/**
	 * 时间对象格式化成String ,等同于java.text.DateFormat.format();
	 * 
	 * @param date
	 *            需要格式化的时间对象
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return 转化结果
	 */
	public static String format(java.util.Date date) {
		return DEFAULT_FORMAT.format(date);
	}

	/**
	 * 时间对象格式化成String ,等同于java.text.SimpleDateFormat.format();
	 * 
	 * @param date
	 *            需要格式化的时间对象
	 * @param regex
	 *            定义格式的字符串
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com    
	 * @return 转化结果
	 */
	public static String format(java.util.Date date, String regex) {
		return getDateFormat(regex).format(date);
	}

	private static EPNDateFormat getDateFormat(String regex) {
		EPNDateFormat fmt = formatMap.get(regex);
		if (fmt == null) {
			fmt = new EPNDateFormat(regex);
			formatMap.put(regex, fmt);
		}
		return fmt;
	}

	/**
	 * 尝试解析时间字符串 ,if failed return null;
	 * 
	 * @author wangp
	 * @since 2008.12.20
	 * @param time
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return
	 */
	public static Date parseByAll(String time) {
		Date stamp = null;
		if (time == null || "".equals(time))
			return null;
		Pattern p3 = Pattern.compile("\\b\\d{2}[.-]\\d{1,2}([.-]\\d{1,2}){0,1}\\b");
		if (p3.matcher(time).matches()) {
			time = (time.charAt(0) == '1' || time.charAt(0) == '0' ? "20" : "19") + time;
		}

		stamp = DateUtils.parse(time, "yyyy-MM-ddHH:mm:ss");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy-MM-dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy.MM.dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy-MM");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy.MM");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy-MM-dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yy-MM-dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy.MM.dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy-MM.dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy.MM-dd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyyMMdd");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy年MM月dd日");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyyMM");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy年MM月");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy");
		if (stamp == null)
			stamp = DateUtils.parse(time, "yyyy年");
		return stamp;
	}

	/**
	 * 解析字符串成时间 ,遇到错误返回null不抛异常
	 * 
	 * @param source
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return 解析结果
	 */
	public static java.util.Date parse(String source) {
		try {
			return DEFAULT_FORMAT.parse(source);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 解析字符串成时间 ,遇到错误返回null不抛异常
	 * 
	 * @param source
	 * @param 格式表达式
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return 解析结果
	 */
	public static java.util.Date parse(String source, String regex) {
		try {
			EPNDateFormat fmt = getDateFormat(regex);
			return fmt.parse(source);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得当前时间的Date对象 ;
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return
	 */
	public static Date getNowDate() {
		return new Date(System.currentTimeMillis());
	}
	
	
	
	public static Date parasetimestamp(String time) {
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Long time2=Long.parseLong(time);	
	    String d = format.format(time2*1000);  
	    Date date;
		try {
			date = format.parse(d);
			 return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	   
	}
	//
	public static Date addDate(Date date,int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);//设置起时间
		cal.add(Calendar.DATE, days);//增加一天  
		return cal.getTime();
	}
	//获取昨天的字符串类型的时间
	public static String yesterday(int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());//设置起时间
		cal.add(Calendar.DATE, -days);//增加一天  
		return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
	}
	//获取昨天的字符串类型的时间
	public static String getdate(int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());//设置起时间
		cal.add(Calendar.DATE, -days);//增加一天  
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}
	
	
	
	
	/**
	 * 获取当前时间字符串
	 * 
	 * 2014年5月5日 下午12:00:00
	 * flyfox 330627517@qq.com
	 * @return
	 */
	public static String getNow() {
		return getNow(DEFAULT_REGEX);
	}
	
	/**
	 * 获取当前时间字符串
	 * 
	 * 2014年7月4日 下午11:47:10
	 * flyfox 330627517@qq.com
	 * @param regex 格式表达式
	 * @return
	 */
	public static String getNow(String regex) {
		return format(getNowDate(), regex);
	}
	
	/***
	 * 获取指定时间所在天的开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrenDayBeginTime(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		return format(ca.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	/***
	 * 获取当前时间的两位年+两位月
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrenDayYearMonth() {
		Calendar ca = Calendar.getInstance();
		ca.setTime(getNowDate());
		return format(ca.getTime(), "yyMM");
	}
	/***
	 * 获取指定时间所在天的结束时间
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrenDayEndTime(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY, 23);
		ca.set(Calendar.MINUTE, 59);
		ca.set(Calendar.SECOND, 59);
		return format(ca.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 字符串时间类型转换返回Date类型
	 * 
	 * @author sunju
	 * @creationDate. 2010-8-27 下午05:23:35
	 * @param date
	 *            字符串时间
	 * @param dateFormat
	 *            时间格式
	 * @return 转换后的时间
	 */
	public static Date dateFormat(String date, String dateFormat) {
		if (StrKit.isBlank(date))
			return null;
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try {
			return format.parse(date);
		} catch (Exception e) {
		
			return null;
		}
	}
	/**
	 * 两个时间时间差[前面时间和比较时间比,小于比较时间返回负数]
	 * 
	 * @author sunju
	 * @creationDate. 2010-8-27 下午05:26:13
	 * @param interval
	 *            比较项，可以是天数、月份、年数、时间、分钟、秒
	 * @param date
	 *            时间
	 * @param compare
	 *            比较的时间
	 * @return 时间差(保留两位小数点,小数点以后两位四舍五入)
	 */
	public static double getDateDiff(int interval, Date date, Date compare) {
		if (date == null || compare == null)
			return 0;
		double result = 0;
		double time = 0;
		Calendar calendar = null;
		switch (interval) {
		case DATE_INTERVAL_DAY:
			time = date.getTime() - compare.getTime();
			result = time / 1000d / 60d / 60d / 24d;
			break;
		case DATE_INTERVAL_HOUR:
			time = date.getTime() - compare.getTime();
			result = time / 1000d / 60d / 60d;
			break;
		case DATE_INTERVAL_MINUTE:
			time = date.getTime() / 1000d / 60d;
			result = time - compare.getTime() / 1000d / 60d;
			break;
		case DATE_INTERVAL_MONTH:
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			time = calendar.get(Calendar.YEAR) * 12d;
			calendar.setTime(compare);
			time -= calendar.get(Calendar.YEAR) * 12d;
			calendar.setTime(date);
			time += calendar.get(Calendar.MONTH);
			calendar.setTime(compare);
			result = time - calendar.get(Calendar.MONTH);
			break;
		case DATE_INTERVAL_SECOND:
			time = date.getTime() - compare.getTime();
			result = time / 1000d;
			break;
		case DATE_INTERVAL_WEEK:
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			time = calendar.get(Calendar.YEAR) * 52d;
			calendar.setTime(compare);
			time -= calendar.get(Calendar.YEAR) * 52d;
			calendar.setTime(date);
			time += calendar.get(Calendar.WEEK_OF_YEAR);
			calendar.setTime(compare);
			result = time - calendar.get(Calendar.WEEK_OF_YEAR);
			break;
		case DATE_INTERVAL_YEAR:
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			time = calendar.get(Calendar.YEAR);
			calendar.setTime(compare);
			result = time - (double) calendar.get(Calendar.YEAR);
			break;
		default:
			break;
		}
		return new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}	
	/**
	 * 获取时间差[前面时间和比较时间比,小于比较时间返回负数]
	 * 
	 * @author sunju
	 * @creationDate. 2010-9-1 下午04:36:07
	 * @param level
	 *            返回时间等级(1:返回天;2:返回天-小时;3:返回天-小时-分4:返回天-小时-分-秒;)
	 * @param date
	 *            时间
	 * @param compare
	 *            比较的时间
	 * @return 时间差(保留两位小数点,小数点以后两位四舍五入)
	 */
	public static String getDateBetween( Date date, Date compare) {
		if (date == null || compare == null)
			return null;
		long s = new BigDecimal(getDateDiff(DATE_INTERVAL_SECOND, date, compare)).setScale(2, BigDecimal.ROUND_HALF_UP).longValue();
		
		int ss = 1;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;
		int ww = dd * 7;
		int mm = ww * 4;
		int yy = mm* 12;
		long year = s / yy;
		long mon = (s - year * yy) / mm;
		long wek = (s - year * yy - mon * mm)  / ww;
		long day = (s - year * yy - mon * mm - wek * ww)  / dd;
		long hour = (s - year * yy - mon * mm - wek * ww -day * dd) / hh;
		long minute = (s -  year * yy - mon * mm - wek * ww -day * dd - hour * hh) / mi;
		long second = (s - year * yy - mon * mm - wek * ww -day * dd - hour * hh - minute * mi) / ss;
		String flag = (day < 0 || hour < 0 || minute < 0 || second < 0 || wek<0 || mon<0 || year <0) ? "-" : "";
		year = Math.abs(year);
		mon = Math.abs(mon);
		wek = Math.abs(wek);
		day = Math.abs(day);
		hour = Math.abs(hour);
		minute = Math.abs(minute);
		second = Math.abs(second);
		StringBuilder result = new StringBuilder(flag);
		if (year != 0) {
			return result.append(year).append("年前").toString();		
		}
			
		if (mon != 0) {
			return result.append(mon).append("月前").toString();			
		}
			
		if (wek != 0) {
			return result.append(wek).append("周前").toString();			
		}

		if (day != 0) {
			return result.append(day).append("天前").toString();			
		}

		if (hour != 0) {
			return result.append(hour).append("小时前").toString();			
		}
		
		if (minute != 0) {
			return result.append(minute).append("分前").toString();		
		}

		if (second != 0) {
			return result.append(second).append("秒前").toString();		
		}
		return result.toString();	
	}
	public static void main(String[] args) {

	}

	/**
     * 获取当月所有天
     * @return
     */
    public static List<String> getDayListOfMonth() {
        List<String> list = new ArrayList<String>();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int year = aCalendar.get(Calendar.YEAR);//年份
        int month = aCalendar.get(Calendar.MONTH) + 1;//月份
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
        	if (i<10) {
        		 String aDate = String.valueOf(year)+"-0"+month+"-0"+i;
                 list.add(aDate);
			}else {
		        String aDate = String.valueOf(year)+"-0"+month+"-"+i;
	            list.add(aDate);
			}
    
        }
        return list;
    }
}

class EPNDateFormat {

	private SimpleDateFormat instance;

	EPNDateFormat() {
		instance = new SimpleDateFormat(DateUtils.DEFAULT_REGEX);
		instance.setLenient(false);
	}

	EPNDateFormat(String regex) {
		instance = new SimpleDateFormat(regex);
		instance.setLenient(false);
	}

	synchronized String format(java.util.Date date) {
		if (date == null)
			return "";
		return instance.format(date);
	}

	synchronized java.util.Date parse(String source) throws ParseException {
		return instance.parse(source);
	}
}
