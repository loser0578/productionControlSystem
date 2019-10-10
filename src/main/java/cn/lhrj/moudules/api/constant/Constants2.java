package cn.lhrj.moudules.api.constant;

public class Constants2 {

	public static final String APP_ID = "wx8337d51821175b90";

	public static final String APP_SECRET = "e280cfb4bb7138eb536ed9749360dfe4";

	// 商户号
	public static final String MCH_ID = "1481486402";

	// API密钥，在商户平台设置
	public static final String API_KEY = "qingtianqiaoxiangnongfa183588815";

	public static final String CREATE_IP = "140.143.56.64";
	
	public static final String notifyUrl ="http://liqin.lcz.fun:8090/api/index/wxnotify";
	

	// 受理模式下给子商户分配的子商户号
	public static final String subMchID = "";

	// HTTPS证书的本地路径
	// public static final String certLocalPath = "/www/server/tomcat/webapps/apiclient_cert.p12";
	public static final String certLocalPath = "apiclient_cert.p12";

	// HTTPS证书密码，默认密码等于商户号MCHID
	public static final String certPassword = MCH_ID;

	// 是否使用异步线程的方式来上报API测速，默认为异步模式
	public static final boolean useThreadToDoReport = true;

	public static final String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	// 以下是几个API的路径：
	// 1）被扫支付API
	public static final String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

	// 2）被扫支付查询API
	public static final String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	// 3）退款API
	public static final String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	// 4）退款查询API
	public static final String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	// 5）撤销API
	public static final String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	// 6）下载对账单API
	public static final String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	// 7) 统计上报API
	public static final String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

}
