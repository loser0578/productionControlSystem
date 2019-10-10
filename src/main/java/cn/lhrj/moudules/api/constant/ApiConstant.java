package cn.lhrj.moudules.api.constant;

public class ApiConstant {

	/**
	 * API参数是否加密
	 */
	public static final boolean ENCRYPT_FLAG = false;
	/**
	 * 是
	 */
	public static final int OPER_YES = 1;
	/**
	 * 否
	 */
	public static final int OPER_NO = 2;
	
	/**
	 * 成功
	 */
	public static final int CODE_SUCCESS = 1;
	/**
	 * 失败
	 */
	public static final int CODE_FAIL = -1;
	/**
	 * 维护
	 */
	/**
	 * 是
	 */
	public static final int DONTNEEDPAY = 3;
	
	
	public static final int CODE_SERVER_MAINTAIN = -9;
	/**
	 * 版本号错误
	 */
	public static final int CODE_VERSION_ERROR = -101;
	/**
	 * 调用方法不存在
	 */
	public static final int CODE_METHOD_ERROR = -103;
	/**
	 * 调用方法异常
	 */
	public static final int CODE_METHOD_HANDLER_ERROR = -104;
	/**
	 * 传递参数异常
	 */
	public static final int CODE_PARAM_ERROR = -102;
	/**
	 * IP黑名单
	 */
	public static final int CODE_IP_BLACK = -201;

	/**
	 * 登陆验证异常
	 */
	public static final int CODE_LOGIN_VALID_ERROR = -501;
	/**
	 * 协议校验失败
	 */
	public static final int CODE_TOKEN_VALID_ERROR = -502;

	public static final int CODE_UNJOINFAMILY_ERROR = -301;

	public static final int CODE_UNAUTH_ERROR = -302;
	
	public static final String MSG_SUCCESS = "success";
	public static final String MSG_FAIL = "fail";
	public static final String MSG_VERSION_ERROR = "版本号错误";
	public static final String MSG_METHOD_ERROR = "调用方法不存在";
	public static final String MSG_METHOD_HANDLER_ERROR = "调用方法异常";
	public static final String MSG_PARAM_ERROR = "传递参数异常";
	public static final String MSG_IP_BLACK = "IP黑名单拦截";
	public static final String MSG_SERVER_MAINTAIN = "API服务维护中";
	public static final String MSG_LOGIN_VALID_ERROR = "登陆验证失败";
	public static final String MSG_CHECKSUM_VALID_ERROR = "协议校验失败";
	public static final String MSG_UNJOINFAMILY_ERROR = "该用户未加入家庭";
	public static final String MSG_UNAUTH_ERROR = "该宝宝未实名认证";
}
