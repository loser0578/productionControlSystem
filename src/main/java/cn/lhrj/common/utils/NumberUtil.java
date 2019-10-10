package cn.lhrj.common.utils;

import java.math.BigDecimal;
import java.util.Random;

public class NumberUtil {

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	// 这个类不能实例化
	private NumberUtil() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static String randomNumString(int length) {
		if (length <= 0) {
			return "";
		}
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer = buffer.append(random.nextInt(10));
		}
		return buffer.toString();
	}

	/**
	 * 取0-bound范围内的随机整数
	 * 
	 * @param bound
	 * @return
	 */
	public static int randomInt(int bound) {
		Random random = new Random();
		return random.nextInt(bound);
	}

	public static void main(String[] args) {
		// for( int i=0 ; i<100; i++ ) {
		// System.out.println(randomInt(6));
		// }
		
		int a = 6683;
		int b = 1000;
		System.out.println((int)NumberUtil.div(a, b));
	}


	/**
	 * 如果是null返回0
	 * 
	 * @param obj
	 * @return
	 */
	public static int parseInt(Object obj) {
		int value = 0;
		if (obj != null) {
			try {
				value = Integer.parseInt(obj.toString());
			} catch (Exception e) {
				value = 0;
			}
		}
		return value;
	}

	/**
	 * 如果是null返回BigDecimal.ZERO
	 * 
	 * @param obj
	 * @return
	 */
	public static BigDecimal parseBigDecimal(Object obj) {
		BigDecimal value = BigDecimal.ZERO;
		if (obj != null) {
			try {
				value = new BigDecimal(obj.toString());
			} catch (Exception e) {
				value = BigDecimal.ZERO;
			}
		}
		return value;
	}
	
	

	
	/**
	 * 将传入的字符串转成int型数据 . 遇到任何错误返回0
	 * @param str 待解析的字符串
	 * @return 解析结果 
	 */
	public static int parseInt(String str){
		return parseInt(str ,0);
	}

	/**
	 * 将传入的字符串转成int型数据 . 遇到任何错误返回replaceWith
	 * @param str 待解析的字符串
	 * @param defaultValue 遇到错误时的替换数字 . 
	 * @return 解析结果 
	 */
	public static int parseInt(String str ,int defaultValue){
		try{
			defaultValue = Integer.parseInt(str);
		} catch(Exception e){}
		return defaultValue ;
	}
	
	/**
	 * 将传入的字符串转成double型数据 . 遇到任何错误返回0
	 * @param str 待解析的字符串
	 * @return 解析结果 
	 */
	public static double parseDbl(String str){
		return parseDbl(str ,0);
	}
	/**
	 * 将传入的字符串转成double型数据 . 遇到任何错误返回replaceWith
	 * @param str 待解析的字符串
	 * @param defaultValue 遇到错误时的替换数字 . 
	 * @return 解析结果 
	 */
	public static double parseDbl(String str ,double defaultValue){
		try{
			defaultValue = Double.parseDouble(str);
		} catch(Exception e){}
		return defaultValue ;
	}
	
	public static float parseFloat(String str) {
		return parseFloat(str, 0);
	}
	
	public static float parseFloat(String str ,float b) {
		try{
			return Float.parseFloat(str);
		}catch(Exception e){
			return b;
		}
	}

	/**
	 * 遇到错误返回0L 
	 * @author 王平
	 * @since 2009.04.30
	 * @param str
	 * @return
	 */
	public static long parseLong(String str) {
		return parseLong(str, 0l);
	}
	/**
	 * 遇到错误返回defaultValue
	 * @author 王平
	 * @since 2009.04.30
	 * @param str
	 * @return
	 */
	public static long parseLong(String str ,long defaultValue){
		try{
			defaultValue = Long.parseLong(str);
		} catch(Exception e){}
		return defaultValue ;
	}
	
	/**
	 * 在数字前面补充0
	 * @author 王平
	 * @since 2009.04.30
	 * @param str
	 * @return
	 */
	public static String addZERO(int num){
		String code="";
		if (num<10) {
			code="00"+String.valueOf(num);
		}else if (num >=10 && num < 100) {
			code="0"+String.valueOf(num);
		}else {
			code=String.valueOf(num);
		} 
		return code;
	}

}
