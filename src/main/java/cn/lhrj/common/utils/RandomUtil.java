package cn.lhrj.common.utils;

import java.util.Random;

/**
 * Description: 验证码生成工具
 * 
 * @author yangh
 * @date 2017年11月14日上午11:48:29
 */
public class RandomUtil {

	public static final String NUMBER_CODES = "012356789";
	public static final String LETTER_CODES = "ABCDEFGHJKLMNPQRSTUVWXYZ";
	public static final String MIX_CODES = "2356789ABCDEFGHJKLMNPQRSTUVWXYZ";

	/**
	 * 纯数字
	 * 
	 * @param verifySize
	 * @return
	 */
	public static String genNumberRandomCode(int verifySize) {
		return genRandomCode(verifySize, NUMBER_CODES);
	}

	/**
	 * 纯字母
	 * 
	 * @param verifySize
	 * @return
	 */
	public static String genLetterRandomCode(int verifySize) {
		return genRandomCode(verifySize, LETTER_CODES);
	}

	/**
	 * 数字字母混合
	 * 
	 * @param verifySize
	 * @param sources
	 * @return
	 */
	public static String genMixRandomCode(int verifySize) {
		return genRandomCode(verifySize, MIX_CODES);
	}

	public static String genRandomCode(int verifySize, String sources) {
		if (sources == null || sources.length() == 0) {
			sources = MIX_CODES;
		}
		int codesLen = sources.length();
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder verifyCode = new StringBuilder(verifySize);
		for (int i = 0; i < verifySize; i++) {
			verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
		}
		return verifyCode.toString();
	}

}
