package cn.lhrj.moudules.api.controller;


import java.util.Map;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.PaymentException;
import com.jfinal.wxaapp.api.WxaAccessToken;
import com.jfinal.wxaapp.api.WxaAccessTokenApi;

import cn.lhrj.common.model.ShopUser;
import cn.lhrj.modules.api.annotation.IgnoreAuth;
import cn.lhrj.modules.api.annotation.NotNullValidate;
import cn.lhrj.moudules.admin.system.config.ConfigCache;
import cn.lhrj.moudules.api.constant.ApiConstant;
import cn.lhrj.moudules.api.model.FullUserInfo;
import cn.lhrj.moudules.api.service.ApiIndexService;
import cn.lhrj.moudules.api.util.ApiBaseAction;
import cn.lhrj.moudules.api.validate.ApiIndexClassifyValidate;

public class ApiIndexController extends ApiBaseAction{
	
	protected Log log=Log.getLog(ApiIndexController.class);
   
	@Inject
	ApiIndexService srv;
	
	@IgnoreAuth
	public void test() {
		/**
		 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
		 @return WxaAccessToken accessToken
		 */
		WxaAccessToken wxaAccessToken = WxaAccessTokenApi.getAccessToken();
		renderJson(toResponsSuccess(wxaAccessToken));
	}
	//获取首页所有分类
	@IgnoreAuth
	public void IndexClassify() {

		Integer page=getPara("page") == null ? 1:getParaToInt("page");
		Integer limit=getPara("limit") == null ? ConfigCache.getValueToInt("limit"):getParaToInt("limit");
		renderJson(ApiBaseAction.toResponsSuccess(srv.IndexClassifypaginate(page, limit)));
	}
	//获取首页滚动图片
	@IgnoreAuth
	public void indexImg() {
		Integer page=getPara("page") == null ? 1:getParaToInt("page");
		Integer limit=getPara("limit") == null ? ConfigCache.getValueToInt("limit"):getParaToInt("limit");
		renderJson(ApiBaseAction.toResponsSuccess(srv.indexImg(page, limit)));
	}
	
	//判断是否已被领取
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public  void getReceive(@Para("orderId") String orderId) {
		log.info("getOrderDetail-orderId"+orderId);
		renderJson(srv.getReceive(orderId));
	}
	
	//充值
	@NotNullValidate(value = "userId",message = "userId不能为空")
	@NotNullValidate(value = "money",message = "money不能为空")
	public void reCharge(@Para("userId")int userId,@Para("money") double money) {
		renderJson(srv.reCharge(userId, money));
	}
	
	//收支明细
	@NotNullValidate(value = "userId",message = "userId不能为空")
	public void consumeLog(@Para("userId")int userId) {
		Integer page=getPara("page") == null ? 1:getParaToInt("page");
		Integer limit=getPara("limit") == null ? 10:getParaToInt("limit");
		renderJson(ApiBaseAction.toResponsSuccess(srv.consumeLog(userId,page,limit)));
	}
	
	//获取用户钱包余额
	@NotNullValidate(value = "userId",message = "userId不能为空")
	public void userMoney(@Para("userId")int userId) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.userMoney(userId)));
	}
	
	//商品详情
	@IgnoreAuth
	@NotNullValidate(value = "goodid",message = "goodid不能为空")
	public void goodsInfo(@Para("goodid") int goodid) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.goodsInfo(goodid)));
	}
	
	//获取面额
	@IgnoreAuth
//	@NotNullValidate(value = "amountid",message = "amountid不能为空")
	public void getAmountid() {
		renderJson(ApiBaseAction.toResponsSuccess(srv.getAmountid()));
	}
	
	//获取所选分类的第一个默认主题
	@IgnoreAuth
    @NotNullValidate(value = "id",message = "id不能为空")
	public void ClassifyDetail(@Para("id") int id) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.getClassifyDetail(id)));
	}

	//获取全部主题分类
	@IgnoreAuth
    @NotNullValidate(value = "id",message = "id不能为空")
	public void AllClassify(@Para("id") int id) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.getAllClassify(id)));
	}
		
	//获取当前分类下的全部主题
	@IgnoreAuth
	@Before(ApiIndexClassifyValidate.class)
    @NotNullValidate(value = "classifyid",message = "分类id不能为空")
	public void getALLCategoryByClassifyId(@Para("classifyid") int classifyid,@Para("page") Integer page,@Para("limit") Integer limit) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.getALLCategoryByClassifyId(classifyid,page, limit)));
	}
	//获取商品分类
	@IgnoreAuth
    @NotNullValidate(value = "id",message = "产品id不能为空")
	public void getGoodsSpecification(@Para("id") int id) {
    	//ShopUser user=getApiLoginAccount();
		ShopUser user=new ShopUser().findById(5);
		renderJson(ApiBaseAction.toResponsSuccess(srv.getGoodsSpecification(user,id)));
	}
	//获取优惠券列表
	//获取商品分类
	@IgnoreAuth
    @NotNullValidate(value = "userid",message = "用户id不能为空")
	@Before(ApiIndexClassifyValidate.class)
	public void getCouponList(@Para("userid") int userid) {
		renderJson(ApiBaseAction.toResponsSuccess(srv.getCouponList(userid)));
	}
   
	/**
     * 根据商品获取可用优惠券列表
     */
	@Before(ApiIndexClassifyValidate.class)
	@NotNullValidate(value = "productid",message = "产品的id不能为空")
	@NotNullValidate(value = "num",message = "购买数量num不能为空")
	public void getCouponListByGoods(@Para("productid") int productid,@Para("num") int num,@Para("page") Integer page,@Para("limit") Integer limit ) {
		renderJson(srv.getPageCouponListByGoods(getApiLoginAccount().getId(),productid,num,page,limit));
	}
	//微信登录
	@IgnoreAuth
    @NotNullValidate(value = "code",message = "code不能为空")
	public void login_by_weixin(@Para("code") String jsCode,@Para("userInfos") String userInfos) {
		FullUserInfo fullUserInfo = null;
        if (null != userInfos) {
        	fullUserInfo=FastJson.getJson().parse(userInfos,  FullUserInfo.class);
  
        }
        if (null == fullUserInfo) {
        	renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "登录失败"));
        	return;
        }
		renderJson(srv.login_by_weixin(jsCode, userInfos,  IpKit.getRealIp(getRequest())));
	}
	//订单提交
	@NotNullValidate(value = "id",message = "产品id不能为空")
	@NotNullValidate(value = "num",message = "购买数量num不能为空")
	@NotNullValidate(value = "formatArr",message = "规格不能为空")
	@NotNullValidate(value = "goodid",message = "goodid不能为空")
	@NotNullValidate(value = "categoryId",message = "categoryId不能为空")
	/*public  void generateOrder(@Para("formatArr") String formatArr,@Para("goodid") Integer goodid,@Para("couponid") Integer couponid,@Para("id") int id,@Para("num") int num,@Para("categoryId") int categoryId) throws PaymentException {
		Ret ret=srv.generateOrder(formatArr,goodid,couponid, getApiLoginAccount().getId(), id, num,categoryId);
		log.info("generateOrder-return"+ret);
		renderJson(ret);
	}*/
	
	//计算价格
	@NotNullValidate(value = "formatArr",message = "规格不能为空")
	@NotNullValidate(value = "num",message = "购买数量num不能为空")
	@NotNullValidate(value = "id",message = "商品id不能为空")
	public void getPrice(@Para("formatArr") String formatArr, @Para("num") int num,@Para("id") long id,@Para("couponid") Integer couponid) {
		renderJson(srv.getPrice(getApiLoginAccount().getId(),formatArr,num,id,couponid));
	}
	//获取订单商品详情
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void getOrderGoods(@Para("orderId") String orderId) {
		renderJson(srv.orderGoodsInfo(orderId));

	}
	//获取订单商品详情
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void getParentOrderGoods(@Para("orderId") String orderId) {
		renderJson(srv.orderParentGoodsInfo(orderId));

	}	
	//查询订单状态接口
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void orderState(@Para("orderId") String orderId){
		log.info("orderState-orderId"+orderId);
		renderJson(srv.orderState(orderId));
	}
	
	//查询充值订单状态接口
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void orderStateByRecharge(@Para("orderId") String orderId){
		log.info("orderState-orderId"+orderId);
		renderJson(srv.orderStateByRecharge(orderId));
	}
	
	//查询所有祝福
	@IgnoreAuth
	public void getBlessings() {
		renderJson(srv.getAllBlessing());
	}
	//赠送他人
	@IgnoreAuth
	@NotNullValidate(value = "sender_name",message = "sender_name不能为空")
	@NotNullValidate(value = "receiver_name",message = "receiver_name不能为空")
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	@NotNullValidate(value = "blessingsname",message = "blessingsname不能为空")
	public void sendToOthers(@Para("sender_name") String sender_name,@Para("receiver_name") String receiver_name,@Para("orderId") String orderId ,@Para("blessingsname") String blessingsname){
		renderJson(srv.sendToOthers(sender_name,receiver_name,orderId,blessingsname));
	}
	
	//获取商品详情
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public  void getOrderDetail(@Para("orderId") String orderId) {
		log.info("getOrderDetail-orderId"+orderId);
		renderJson(srv.getOrderDetail(orderId));
	}
	@IgnoreAuth
	public void wxnotify() {
		try {
			log.info("---------开始接受通知----------");

			String xmlMsg = HttpKit.readData(getRequest());
			// 将解析结果存储在HashMap中
			Map<String, String> requestMap = PaymentKit.xmlToMap(xmlMsg);
			String return_code = requestMap.get("return_code");
			String return_msg = requestMap.get("return_msg");
			log.info("---------开始接受通知---return_code:" + return_code);
			log.info("---------开始接受通知---return_msg:" + return_msg);
			String serial_number = ""; // 交易流水号
			String order_code = ""; // 订单号
			String to_section_money = "0";// 订单金额
			for (String key : requestMap.keySet()) {
				log.info("key=------- " + key + " and value=---- " + requestMap.get(key));
				if ("transaction_id".equals(key)) {
					serial_number = requestMap.get(key);
				} else if ("out_trade_no".equals(key)) {
					order_code = requestMap.get(key);
				} 
			}
			log.info("交易流水号serial_number:" + serial_number);
			log.info("订单号order_code:" + order_code);
			log.info("订单金额to_section_money:" + to_section_money);
			//srv.dealPay(order_code, return_code);				
			renderJson("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage(), e);
			renderJson("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
		}
	}
	//邮寄收货。
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	@NotNullValidate(value = "userName",message = "userName不能为空")
	@NotNullValidate(value = "telNumber",message = "telNumber不能为空")
	@NotNullValidate(value = "cityName",message = "cityName不能为空")
	@NotNullValidate(value = "provinceName",message = "provinceName不能为空")
	@NotNullValidate(value = "countyName",message = "countyName不能为空")
	@NotNullValidate(value = "detailInfo",message = "detailInfo不能为空")
	@NotNullValidate(value = "postalCode",message = "postalCode不能为空")
	public void accept(@Para("orderId") String orderId,@Para("userName") String userName,@Para("telNumber") String telNumber,@Para("cityName") String cityName,@Para("provinceName") String provinceName,@Para("countyName") String countyName,@Para("detailInfo") String detailInfo,@Para("postalCode") String postalCode,@Para("remark") String remark) {
		renderJson(srv.accept(getApiLoginAccount().getId(), orderId, userName, telNumber, cityName, provinceName, countyName, detailInfo, remark, postalCode));
	}
	
	//赠送他人
	public void otheraccept(@Para("orderId") String orderId) {
		renderJson(srv.otheraccept(orderId, getApiLoginAccount().getId()));
	}
	
	//填写已经收到礼卡的信息（补充）；
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	@NotNullValidate(value = "userName",message = "userName不能为空")
	@NotNullValidate(value = "telNumber",message = "telNumber不能为空")
	@NotNullValidate(value = "cityName",message = "cityName不能为空")
	@NotNullValidate(value = "provinceName",message = "provinceName不能为空")
	@NotNullValidate(value = "countyName",message = "countyName不能为空")
	@NotNullValidate(value = "detailInfo",message = "detailInfo不能为空")
	@NotNullValidate(value = "postalCode",message = "postalCode不能为空")
	public void resetAddress(@Para("orderId") String orderId,@Para("userName") String userName,@Para("telNumber") String telNumber,@Para("cityName") String cityName,@Para("provinceName") String provinceName,@Para("countyName") String countyName,@Para("detailInfo") String detailInfo,@Para("postalCode") String postalCode,@Para("remark") String remark) {
		// TODO Auto-generated method stub
				if ( null==userName || "".equals(userName) ) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "收货人不能为空"));
					return;
				}else if (null==orderId || "".equals(orderId)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "订单号不能为空"));
					return;
				}else if (null==telNumber || "".equals(telNumber)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "手机号不能为空"));
					return;
				}else if (null==cityName || "".equals(cityName)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
					return;
				}else if (null==provinceName || "".equals(provinceName)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
					return;
				}else if (null==countyName || "".equals(countyName)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
					return;
				}else if (null==detailInfo || "".equals(detailInfo)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "详细地址不能为空"));
					return;
				}else if (null==postalCode || "".equals(postalCode)) {
					renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "邮编不能为空"));
					return;
				}else {
					renderJson(srv.resetAddress(getApiLoginAccount().getId(), orderId, userName, telNumber, cityName, provinceName, countyName, detailInfo, remark, postalCode));
				}
	}
	//他人填写收获信息
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	@NotNullValidate(value = "userName",message = "userName不能为空")
	@NotNullValidate(value = "telNumber",message = "telNumber不能为空")
	@NotNullValidate(value = "cityName",message = "cityName不能为空")
	@NotNullValidate(value = "provinceName",message = "provinceName不能为空")
	@NotNullValidate(value = "countyName",message = "countyName不能为空")
	@NotNullValidate(value = "detailInfo",message = "detailInfo不能为空")
	@NotNullValidate(value = "postalCode",message = "postalCode不能为空")
	public void otherAccpetUpdate(@Para("orderId") String orderId,@Para("userName") String userName,@Para("telNumber") String telNumber,@Para("cityName") String cityName,@Para("provinceName") String provinceName,@Para("countyName") String countyName,@Para("detailInfo") String detailInfo,@Para("postalCode") String postalCode,@Para("remark") String remark) {
		// TODO Auto-generated method stub
		if ( null==userName || "".equals(userName) ) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "收货人不能为空"));
			return;
		}else if (null==orderId || "".equals(orderId)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "订单号不能为空"));
			return;
		}else if (null==telNumber || "".equals(telNumber)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "手机号不能为空"));
			return;
		}else if (null==cityName || "".equals(cityName)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
			return;
		}else if (null==provinceName || "".equals(provinceName)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
			return;
		}else if (null==countyName || "".equals(countyName)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "地址不能为空"));
			return;
		}else if (null==detailInfo || "".equals(detailInfo)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "详细地址不能为空"));
			return;
		}else if (null==postalCode || "".equals(postalCode)) {
			renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "邮编不能为空"));
			return;
		}else {
			renderJson(srv.otherAccpetUpdate(getApiLoginAccount().getId(), orderId, userName, telNumber, cityName, provinceName, countyName, detailInfo, remark, postalCode));
		}
	}
	
	
	/*
	 * 接下来是我的页面
	 */

	@Before(ApiIndexClassifyValidate.class)
	//获取我购买的礼卡
	public void getOrderList(@Para("page") Integer page,@Para("limit") Integer limit) {
		renderJson(srv.getOrderList(getApiLoginAccount().getId(), page, limit));
	}

	@Before(ApiIndexClassifyValidate.class)
	//获取我收到的礼卡列表
	public void getReciverOrderList(@Para("page") Integer page,@Para("limit") Integer limit) {
		renderJson(srv.getOrderReciverList(getApiLoginAccount().getId(), page, limit));
	}
	//获取优惠券列表

	@Before(ApiIndexClassifyValidate.class)
	public void counList(@Para("page") Integer page,@Para("limit") Integer limit) {
		renderJson(srv.getPageCounList(page, limit,getApiLoginAccount().getId()));
	}
	//获取购买的详情
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void getOrderDetailInfo(@Para("orderId") String orderId) {
		renderJson(srv.getOrderDetailInfo(orderId));
	}
	//获取接收的详情
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void getOrderDetailReciverInfo(@Para("orderId") String orderId) {
		renderJson(srv.getOrderReciverDetailInfo(orderId));
	}
	//查看物流信息接口
	@IgnoreAuth
	@NotNullValidate(value = "orderId",message = "orderId不能为空")
	public void getTrack(@Para("orderId") String orderId) throws Exception {
		renderJson(srv.getTrack(orderId));
	}
	//兑换优惠券
	@NotNullValidate(value = "code",message = "code不能为空")
	public void  exchangeCounpon(@Para("code") String code) {
		// TODO Auto-generated method stub
		renderJson(srv.exchange(code, getApiLoginAccount().getId()));
	}
	//获取我的页面的初始值

	public void getMyData() {
        renderJson(srv.GetMyInfo(getApiLoginAccount().getId()));
	}
	
	//清除缓存
	@IgnoreAuth
	public void cacheInit() {
		ConfigCache.init();
		renderJson(Ret.ok());
	}
}
