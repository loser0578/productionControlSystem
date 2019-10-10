package cn.lhrj.moudules.api.service;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Duang;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.PaymentException;
import com.jfinal.wxaapp.api.WxaOrder;
import com.jfinal.wxaapp.api.WxaUserApi;

import cn.hutool.core.util.RandomUtil;
import cn.lhrj.common.model.ShopBlessing;
import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.ShopConsumeLog;
import cn.lhrj.common.model.ShopDenomination;
import cn.lhrj.common.model.ShopGoods;
import cn.lhrj.common.model.ShopGoodsSpecification;
import cn.lhrj.common.model.ShopIndexImg;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopOrderGoods;
import cn.lhrj.common.model.ShopOrderLog;
import cn.lhrj.common.model.ShopOrderReciver;
import cn.lhrj.common.model.ShopProduct;
import cn.lhrj.common.model.ShopUser;
import cn.lhrj.common.model.ShopUserCoupon;
import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.RandomStrUtils;
import cn.lhrj.common.utils.StrUtils;
import cn.lhrj.moudules.api.constant.ApiConstant;
import cn.lhrj.moudules.api.constant.Constants2;
import cn.lhrj.moudules.api.model.UserInfo;
import cn.lhrj.moudules.api.util.ApiBaseAction;
import cn.lhrj.moudules.api.util.XcxPayUtil;
import cn.lhrj.moudules.index.MessageService;
import cn.lhrj.moudules.shop.goodsspecification.GoodsSpecificationService;
import cn.lhrj.moudules.shop.ordergoods.OrderGoodsService;

public class ApiIndexService {
	protected Log log=Log.getLog(ApiIndexService.class);
	public Kv IndexClassifypaginate(int page,int limit) {
		
		SqlPara sPara=Db.getSqlPara("category.queryIndexClassify");
		Page<Record> records=Db.paginate(page,limit,sPara);
		return Kv.by("page",records);
		
	}
	
	public Kv indexImg(int page,int limit) {
		Page<ShopIndexImg> indexImgPage = new ShopIndexImg().paginate(page, limit, "select * ","from shop_index_img where is_show = 1 ORDER BY short ASC ");
		return Kv.by("list", indexImgPage);
	}
	
	public Kv goodsInfo(int id) {
		ShopGoods goods = new ShopGoods().findFirst("select name,retail_price,goods_brief,goods_desc from shop_goods where id = ?",id);
		String imgUrlString = goods.getGoodsBrief();
		JSONArray array = JSONArray.parseArray(imgUrlString);
		goods.put("imgList", array);
		return Kv.by("goods", goods);
	}
	
	public Ret reCharge(int id,double denoMoney) {
		ShopUser user = new ShopUser().findFirst("select * from shop_user where id = ?",id);
		//用户原本的钱
		double beforeMoney = user.getMoney();
		//变更之后的钱
		double aftermoney = denoMoney + beforeMoney;
		user.setMoney(aftermoney);
		//订单号
		String orderSn = RandomStrUtils.generateOrderNumber();
		//新增log
		boolean result = Db.tx(() -> {
			ShopConsumeLog consumeLog = new ShopConsumeLog();
			consumeLog.setUserId(id);
			consumeLog.setCreateTime(DateUtils.getNowDate());
			consumeLog.setUpdateTime(DateUtils.getNowDate());
			consumeLog.setOrderId(orderSn);
			consumeLog.setChangeMoney(denoMoney);
			consumeLog.setType(2);
			consumeLog.setStatus(1);
			consumeLog.setState(1);
			consumeLog.setBeforeMoney(beforeMoney);
			consumeLog.setAfterMoney(aftermoney);
			consumeLog.setChangeMoney(denoMoney);
			boolean b1 = consumeLog.save();
			if (b1) {
				return true;
			}else {
				return false;
			}
		});
		if (!result) {
        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "充值失败");
		}
		//提交订单
        WxaOrder order=new WxaOrder(Constants2.APP_ID, Constants2.MCH_ID, Constants2.API_KEY);
        order.setBody("用户充值");
        order.setOutTradeNo(orderSn);
        Double denoMoneyX100 = denoMoney * 100;
        int denoMoneyToInt = denoMoneyX100.intValue();
        String total=String.valueOf(denoMoneyToInt);
        order.setTotalFee(total);
        order.setNotifyUrl(Constants2.notifyUrl);
        order.setSpbillCreateIp(Constants2.CREATE_IP);
        if (null != user) {
        	 order.setOpenId(user.getWeixinOpenid());
		}
        Map<String,String> map;
		try {
			  map = new XcxPayUtil().unifiedOrder(order);
			  map.put("ordersn", orderSn);
			  return  ApiBaseAction.toResponsSuccess(map);
		} catch (PaymentException e) {
			log.error(e.getMessage(), e);			
			e.printStackTrace();
			return  ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, e.getMessage());
		}
	}
	
	public Kv consumeLog(int uid,int page,int limit) {
		Page<ShopConsumeLog> logs = new ShopConsumeLog().paginate(page,limit,"select a.*,c.goods_name ","from shop_consume_log a left join shop_order b on b.order_sn = a.order_id left join shop_order_goods c on c.order_id = b.id where a.user_id = ? and a.type = 1 and a.change_money != 0 order by id desc",uid);
		List<ShopConsumeLog> logList = logs.getList();
		
		for (int i = 0; i < logList.size(); i++) {
			ShopConsumeLog log = logList.get(i);
			if (log.getState() == 0) {
				//支出
				String showMoney = "-"+log.getChangeMoney();
				log.put("showMoney", showMoney);
				//商品名
				String goodsName = "购买"+log.get("goods_name");
				log.put("goodsName",goodsName);
			}else if(log.getState() == 1){
				//充值类型
				log.put("goodsName","用户充值");
				String showMoney = "+"+log.getChangeMoney();
				log.put("showMoney", showMoney);
			}else {
				log.put("goodsName","后台充值");
				String showMoney = "+"+log.getChangeMoney();
				log.put("showMoney", showMoney);
			}
		}
		return Kv.by("list", logs);
	}
	
	public Kv userMoney(int id) {
		ShopUser user = new ShopUser().findFirst("select * from shop_user where id = ?",id);
		double money = user.getMoney();
		DecimalFormat df = new DecimalFormat("#.##");
		return Kv.by("money", df.format(money));
	}
	
	public Kv getAmountid() {
		List<ShopDenomination> denominations = new ShopDenomination().find("select * from shop_denomination where _status = 1 order by denomination asc");
		return Kv.by("list", denominations);
	}
	
	public Kv getClassifyDetail(int id) {
		Record records=null;
		SqlPara sPara=Db.getSqlPara("category.ApiqueryObject",id);
		records=Db.findFirst(sPara);
		if (records == null ) {
			SqlPara sPara2=Db.getSqlPara("category.queryObject",id);
			records=Db.findFirst(sPara2);
		}
		return Kv.by("list",records);
	}

	
	public Kv getAllClassify(int id) {
		ShopCategory category=new ShopCategory().findById(id);
		SqlPara sPara=Db.getSqlPara("categoryclassfy.queryList",Kv.by("categoryid", category.getParentId()));
		List<Record> records=Db.find(sPara);
		return Kv.by("list",records);
	}
	public Kv getALLCategoryByClassifyId(int id,int page,int limit) {
		SqlPara sPara=Db.getSqlPara("category.queryList",Kv.by("classfyId", id));
		Page<Record> records=Db.paginate(page, limit, sPara);
		return Kv.by("list",records);
	}
	
	public Kv getGoodsSpecification(ShopUser user,int id) {
		ShopCategory shopCategory=new ShopCategory().findFirst("select p.goods_id from shop_category c left join shop_category p on c.parent_id=p.id where c.id=? ",id);
		Long goodsid=(long) 0;
		if (shopCategory!=null) {
			goodsid=shopCategory.getGoodsId();
		}
		ShopGoods goods=new ShopGoods().findFirst("select id,primary_pic_url,retail_price,market_price from shop_goods where id=?",goodsid);		
		SqlPara sPara=Db.getSqlPara("goodsspecification.ApiqueryList",Kv.by("goodsId", goodsid));
		List<Record> records=Db.find(sPara);
		Kv kv=new Kv();
		JSONArray array=new JSONArray();
		if (records.size()>0) {
			for (int i = 0; i < records.size(); i++) {
				int specification_id=records.get(i).getInt("specification_id");
				JSONObject object=new JSONObject();
				List<ShopGoodsSpecification> sp=new ShopGoodsSpecification().find("select gs.id,gs.value,gs.specification_id,s.name from shop_goods_specification gs left join shop_specification s on gs.specification_id = s.id where gs.specification_id=? and gs.goods_id=? order by gs.id ",specification_id,goodsid);
				object.put("name", sp.get(i).get("name"));
				object.put("list", sp);
				object.put("specification_id", sp.get(i).get("specification_id"));
				array.add(object);
			}
		}
		kv.set("specificationList", array);
		kv.set("productList", getProductList(goodsid).get("productList"));
		kv.set("goods", goods);
		kv.set("couponList", getCouponList(user.getId()).get("list"));
		kv.set("categoryId", id);

		return kv;
	}
	//获取优惠券列表
	public Kv getCouponList(long userid) {
		SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("user_id", userid).set("coupon_status", 1));
		List<Record> records=Db.find(sPara);
        for (Record couponVo : records) {
            if (couponVo.getInt("coupon_status")==1) {
                // 检查是否过期
                if(couponVo.getDate("use_end_date").before(new Date())) {
                	Db.update("update shop_user_coupon set coupon_status=3 where id = ?", couponVo.getInt("id"));
                }
            }
            if (couponVo.getInt("coupon_status")==3) {
                // 检查是否不过期
                if(couponVo.getDate("use_end_date").after(new Date())) {
                	Db.update("update shop_user_coupon set coupon_status=1 where id = ?", couponVo.getInt("id"));
                }
            }
        }

		return Kv.by("list",records);
	}
	//获取优惠券列表
	public Page<Record> getCouponPage(BigDecimal goodsTotalPrice, long userid,int page,int limit) {
		SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("user_id", userid).set("good_price",goodsTotalPrice).set("coupon_status", 1));
		Page<Record> records=Db.paginate(page, limit, sPara);
        for (Record couponVo : records.getList()) {
            if (couponVo.getInt("coupon_status")==1) {
                // 检查是否过期
                if(couponVo.getDate("use_end_date").before(new Date())) {  
                    Db.update("update shop_user_coupon set coupon_status=3 where id = ?", couponVo.getInt("user_coupon_id"));
                }
            }
            if (couponVo.getInt("coupon_status")==3) {
                // 检查是否不过期
                if(couponVo.getDate("use_end_date").after(new Date())) {
                	Db.update("update shop_user_coupon set coupon_status=1 where id = ?", couponVo.getInt("user_coupon_id"));
                }
            }
        }
		return records;
	}
	//通过商品获取优惠券  分页

	public Ret getPageCouponListByGoods(long userid,int productid,int num,int page,int limit) {
		BigDecimal goodsTotalPrice = new BigDecimal(0.00);
		ShopProduct product=new ShopProduct().findById(productid);
		if (productid==0) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "请选择规格");
		}
		goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(num));
		Page<Record> kv= getCouponPage(goodsTotalPrice,userid,page,limit);

       return ApiBaseAction.toResponsSuccess(Kv.by("list", kv));
      
	}
	//通过商品获取优惠券不分页
	@SuppressWarnings("unchecked")
	public Kv getCouponListByGoods(long userid,int productid,int num) {
		BigDecimal goodsTotalPrice = new BigDecimal(0.00);
		ShopProduct product=new ShopProduct().findById(productid);
		goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(num));
		Kv kv= getCouponList(userid);
		List<Record> list=(List<Record>) kv.get("list");
		if (list.size()>0) {
			for(int i=list.size()-1; i>=0; i--) {
	            if (goodsTotalPrice.compareTo(list.get(i).getBigDecimal("min_goods_amount")) >= 0) { // 可用优惠券
	        
	            } else {
	            	
	            	list.remove(i);
	                
	            }	
				
			}
		}
	

       
        return Kv.by("list", kv);
	}
	//获取产品列表
	public Kv getProductList(Long goodsid) {
		SqlPara sPara=Db.getSqlPara("product.queryList",Kv.by("goodsId", goodsid));
		List<Record> records=Db.find(sPara);
		
		return Kv.by("productList", records);
	}
	//微信登陆
	public Ret login_by_weixin(String jsCode,String userInfos,String ip) {
		
		UserInfo fullUserInfo = null;
        if (null != userInfos) {
        	fullUserInfo=FastJson.getJson().parse(userInfos,  UserInfo.class);
  
        }
        if (null == fullUserInfo) {
        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "登录失败");
        
        }
		WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);
		ApiResult apiResult = wxaUserApi.getSessionKey(jsCode);	
        if (null == apiResult || StrKit.isBlank(apiResult.getStr("openid"))) {
        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "登录失败");
        }

		ShopUser userVo=new ShopUser().findFirst("select * from shop_user where weixin_openid =? ",apiResult.getStr("openid"));
        long uid;
		if (null == userVo) {
            ShopUser user=new ShopUser();
            user.setUsername("微信用户" + RandomUtil.randomString(12));
            user.setPassword(apiResult.getStr("openid"));
            user.setRegisterTime(new Date());
            user.setRegisterIp(ip);
            user.setLastLoginIp(user.getRegisterIp());
            user.setLastLoginTime(user.getRegisterTime());
            user.setWeixinOpenid(apiResult.getStr("openid"));
            user.setAvatar(fullUserInfo.getAvatarUrl());
            //性别 0：未知、1：男、2：女
            user.setGender(fullUserInfo.getGender());
            user.setNickname(fullUserInfo.getNickName());
            user.save();
            uid=user.getId();
        } else {
        	userVo.setAvatar(fullUserInfo.getAvatarUrl());
        	userVo.setNickname(fullUserInfo.getNickName());
            userVo.setLastLoginIp(ip);
            userVo.setLastLoginTime(new Date());
            userVo.update();
            uid=userVo.getId();
        }
        
        Map<String, Object> tokenMap = TokenService.createToken(uid);
        String token = (String) tokenMap.get("token");
        if (null == fullUserInfo || StrKit.isBlank(token)) {
        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "登录失败");
        }
        Map<String, Object> resultObj = new HashMap<String, Object>();
        resultObj.put("token", token);
        resultObj.put("userInfo", fullUserInfo);
        resultObj.put("userId", uid);
        System.out.println("openid"+apiResult.getStr("openid"));
        return ApiBaseAction.toResponsSuccess(resultObj);
	}
	
    private String[] getSpecificationIdsArray(String ids) {
        String[] idsArray = null;
        if (!StrKit.isBlank(ids)) {
            String[] tempArray = ids.split("_");
            if (null != tempArray && tempArray.length > 0) {
                idsArray = tempArray;
            }
        }
        return idsArray;
    }
	//提交订单
	/*@SuppressWarnings({"unchecked", "rawtypes" })
	public Ret generateOrder(String specificationIds,Integer goodsid,Integer couponId,int userid,int productid,int num,int categoryId) {
		
		ShopUser shopUser = new ShopUser().findFirst("select * from shop_user where id = ?",userid);
		if (null == shopUser) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "找不到用户");
		}
		//用户钱包的钱
		double beforeMoney = shopUser.getMoney();
		//System.out.println("用户的钱"+beforeMoney);
		JSONArray array=JSONArray.parseArray(specificationIds);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) == null) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "请完善规格");
			}
			sb.append(array.getJSONObject(i).get("id"));
			sb.append("_");
		}
		//sb.delete(sb.length()-1,sb.length());
		List<ShopProduct> products=new ShopProduct().find("SELECT * FROM `shop_product` WHERE `goods_specification_ids` = ? AND `goods_id` = ? ",sb.toString(),goodsid);
		if (products.size()>0) {
			if (products.get(0).getId()!=productid) {
				 return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "分类与商品不符");
			}
		}else {
			 return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此商品");
		}
			
		//获取产品的信息
		ShopProduct product=new ShopProduct().findById(productid);
		//获取商品信息
	    ShopGoods goods=new ShopGoods().findById(product.getGoodsId());
	    if (null == goods || goods.getIsDelete() == 1 || goods.getIsOnSale() != 1) {
	    	 return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "商品已下架");
	    }
	    if (null == product || product.getGoodsNumber() < num) {
	    	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "商品已下架");
	    }
	    //添加规格名和值
	    String[] goodsSepcifitionValue = null;
	    if (null != product.getGoodsSpecificationIds() && product.getGoodsSpecificationIds().length() > 0) {
	        Kv specificationParam=new Kv();
	        String[] idsArray = getSpecificationIdsArray(product.getGoodsSpecificationIds());
	        specificationParam.put("ids", idsArray);
	        specificationParam.put("goodsId", goods.getId());
	        List<Record> specificationEntities = new  GoodsSpecificationService().queryAll(specificationParam);
	        goodsSepcifitionValue = new String[specificationEntities.size()];
	        for (int i = 0; i < specificationEntities.size(); i++) {
	            goodsSepcifitionValue[i] = specificationEntities.get(i).getStr("value");
	        }
	    }	
	    
	    final String finalgoodsSepcifitionValue=StrUtils.join(goodsSepcifitionValue, ":");
		Integer freightPrice = 0;
	    Record couponVo = null;
	    //获得使用的优惠券
	    BigDecimal couponPrice = new BigDecimal(0.00);
	    if (couponId != null && couponId != 0) {
			SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("couponId", couponId).set("coupon_status",1));
			couponVo=Db.findFirst(sPara);
	        if (couponVo != null && couponVo.getInt("coupon_status") == 1) {
	            couponPrice = couponVo.getBigDecimal("type_money");
	        }
	    }
	    BigDecimal finalcouponPrice=couponPrice;
	    //计算订单的费用
		BigDecimal goodsTotalPrice = new BigDecimal(0.00);
	
		goodsTotalPrice = product.getRetailPrice().multiply(new BigDecimal(num));
	    //商品总价
		goodsTotalPrice = product.getBigDecimal("retail_price").multiply(new BigDecimal(num));  
		BigDecimal finalgoodsTotalPrice=goodsTotalPrice;
		
		//订单价格计算
	    BigDecimal orderTotalPrice = goodsTotalPrice.add(new BigDecimal(freightPrice)); //订单的总价
	
	    BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice);  //减去其它支付的金额后，要实际支付的金额
	    System.out.println("实际付款的价格"+actualPrice.floatValue());
	    
	    //加入优惠券的金额 可以抵消商品的总价， 就直接显示支付成功。
	    //优惠卷不够扣款,钱包的钱也不够扣款 需要调用微信支付
	    if (actualPrice.floatValue()>0&&actualPrice.floatValue()>beforeMoney) {
	    	System.out.println("走1");
	    	//需要再补交的金额
	        double changeMoney = (actualPrice.doubleValue())-beforeMoney;
	        final Ret ret= new Ret();
			boolean isok=Db.tx(new IAtom() {
		        public boolean run()  {	     
		            //写入订单
		            ShopOrder orderInfo = new ShopOrder();
		            String orderSn = RandomStrUtils.generateOrderNumber();
		            orderInfo.setOrderSn(orderSn);
		            orderInfo.setUserId(userid);
		            orderInfo.setCouponId(couponId);
		            orderInfo.setCouponPrice(finalcouponPrice);
		            orderInfo.setAddTime(new Date());
		            orderInfo.setGoodsPrice(finalgoodsTotalPrice);
		            orderInfo.setOrderPrice(orderTotalPrice);
		            orderInfo.setActualPrice(actualPrice);
		            // 待付款
		            orderInfo.setOrderStatus(0);
		            orderInfo.setShippingStatus(0);
		            orderInfo.setPayStatus(1);
		            orderInfo.setShippingId(0);
		            orderInfo.setShippingFee(new BigDecimal(0));
		            orderInfo.setIntegral(0);
		            orderInfo.setIntegralMoney(new BigDecimal(0));
		            orderInfo.setOrderType("4"); //直接购买
		            boolean b1=orderInfo.save();
		            //购买日志
		            ShopConsumeLog log = new ShopConsumeLog();
		            log.setUserId(userid);
		            log.setOrderId(orderSn);
		            log.setCreateTime(DateUtils.getNowDate());
		            log.setUpdateTime(DateUtils.getNowDate());
		            log.setStatus(1);
		            log.setState(0);
		            
		            log.setBeforeMoney(beforeMoney);
		            log.setChangeMoney(beforeMoney);
		            log.setAfterMoney(0.00);
		            boolean b3 = log.save();
		            
		            ret.set("ordersn", orderInfo.getOrderSn());
		            ret.set("goodprice",finalgoodsTotalPrice);
		            ret.set("orderPrice",orderTotalPrice);
		            ret.set("actualPrice",actualPrice);
		            //统计商品总价
		            ShopOrderGoods orderGoodsVo = new ShopOrderGoods();
		            orderGoodsVo.setOrderId(orderInfo.getId());
		            orderGoodsVo.setGoodsId(goods.getId());
		            orderGoodsVo.setGoodsSn(goods.getGoodsSn());
		            orderGoodsVo.setProductId(product.getId());
		            orderGoodsVo.setGoodsName(goods.getName());
		            orderGoodsVo.setListPicUrl(goods.getListPicUrl());
		            orderGoodsVo.setMarketPrice(product.getMarketPrice());
		            orderGoodsVo.setRetailPrice(product.getRetailPrice());
		            orderGoodsVo.setNumber(num);
		            orderGoodsVo.setcategoryId(categoryId);
		            orderGoodsVo.setGoodsSpecifitionNameValue(finalgoodsSepcifitionValue);
		            orderGoodsVo.setGoodsSpecifitionIds(product.getGoodsSpecificationIds());	
		            orderGoodsVo.setnumberRemainr(num);
		            boolean b2=orderGoodsVo.save();
		            if (couponId != null && couponId != 0) {
		            	ShopUserCoupon couponVo2=new ShopUserCoupon().findById(couponId);
	        		    couponVo2.set("coupon_status", 2);
	        		    couponVo2.set("order_id", orderInfo.getId());
	        		    couponVo2.set("used_time", new Date());
	        		    couponVo2.update();
					}
		            return b1 && b2 && b3;
		        }        
	             
			});
	        if (!isok) {
	        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "订单提交失败");
			}
	        //提交订单
	        WxaOrder order=new WxaOrder(Constants2.APP_ID, Constants2.MCH_ID, Constants2.API_KEY);
	        order.setBody(goods.getName());
	        order.setOutTradeNo(ret.getStr("ordersn"));
	        Double changeMoneyX100 = changeMoney * 100;
	        int changeMoneyToInt = changeMoneyX100.intValue();
	        String total=String.valueOf(changeMoneyToInt);
	        order.setTotalFee(total);
	        order.setNotifyUrl(Constants2.notifyUrl);
	        order.setSpbillCreateIp(Constants2.CREATE_IP);
	        ShopUser user=new ShopUser().findById(userid);
	        if (null != user) {
	        	 order.setOpenId(user.getWeixinOpenid());
			}
	     
	        Map map;
			try {
				  map = new XcxPayUtil().unifiedOrder(order);
				  map.put("ordersn", ret.getStr("ordersn"));
				  return  ApiBaseAction.toResponsSuccess(map);
			} catch (PaymentException e) {
				log.error(e.getMessage(), e);			
				e.printStackTrace();
				return  ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, e.getMessage());
			}
		}else if (actualPrice.floatValue()>0&&actualPrice.floatValue()<=beforeMoney) {
			System.out.println("走2");
			//扣除钱包的钱 不需要微信支付
			final Ret ret= new Ret();
			boolean isok=Db.tx(new IAtom() {
		        public boolean run()  {	     
		            //写入订单
		        	ShopUser user=new ShopUser().findById(userid);
		            ShopOrder orderInfo = new ShopOrder();
		            String orderSn = RandomStrUtils.generateOrderNumber();
		            orderInfo.setOrderSn(orderSn);
		            orderInfo.setUserId(userid);
		            orderInfo.setCouponId(couponId);
		            orderInfo.setCouponPrice(finalcouponPrice);
		            orderInfo.setAddTime(new Date());
		            orderInfo.setGoodsPrice(finalgoodsTotalPrice);
		            orderInfo.setOrderPrice(orderTotalPrice);
		            orderInfo.setActualPrice(new BigDecimal(0));
		            // 订单已付款
		            orderInfo.setOrderStatus(201);
		            orderInfo.setShippingStatus(0);
		            orderInfo.setPayStatus(2);
		            orderInfo.setShippingId(0);
		            orderInfo.setShippingFee(new BigDecimal(0));
		            orderInfo.setIntegral(0);
		            orderInfo.setIntegralMoney(new BigDecimal(0));
		            orderInfo.setPayName(user.getNickname());
		            orderInfo.setPayTime(new Date());
		            orderInfo.setOrderType("5"); //直接购买
		            boolean b1=orderInfo.save();
		            ret.set("ordersn", orderInfo.getOrderSn());
		            ret.set("goodprice",finalgoodsTotalPrice);
		            ret.set("orderPrice",orderTotalPrice);
		            ret.set("actualPrice",actualPrice);
		            //收付日志
		            ShopConsumeLog log = new ShopConsumeLog();
		            log.setUserId(userid);
		            log.setOrderId(orderSn);
		            log.setCreateTime(DateUtils.getNowDate());
		            log.setUpdateTime(DateUtils.getNowDate());
		            log.setStatus(1);
		            log.setState(0);
		            log.setType(1);
		            log.setBeforeMoney(beforeMoney);
		            //钱包剩余金额
		            double changeMoney = beforeMoney - actualPrice.doubleValue();
		            log.setAfterMoney(changeMoney);
		            log.setChangeMoney(actualPrice.doubleValue());
		            boolean b3 = log.save();
		            //更新用户钱包
		            shopUser.setMoney(changeMoney);
		            boolean b4 = shopUser.update();
		            //统计商品总价
		            ShopOrderGoods orderGoodsVo = new ShopOrderGoods();
		            orderGoodsVo.setOrderId(orderInfo.getId());
		            orderGoodsVo.setGoodsId(goods.getId());
		            orderGoodsVo.setGoodsSn(goods.getGoodsSn());
		            orderGoodsVo.setProductId(product.getId());
		            orderGoodsVo.setGoodsName(goods.getName());
		            orderGoodsVo.setcategoryId(categoryId);
		            orderGoodsVo.setListPicUrl(goods.getListPicUrl());
		            orderGoodsVo.setMarketPrice(product.getMarketPrice());
		            orderGoodsVo.setRetailPrice(product.getRetailPrice());
		            orderGoodsVo.setNumber(num);
		            orderGoodsVo.setGoodsSpecifitionNameValue(finalgoodsSepcifitionValue);
		            orderGoodsVo.setGoodsSpecifitionIds(product.getGoodsSpecificationIds());	
		            orderGoodsVo.setnumberRemainr(num);
		            boolean b2=orderGoodsVo.save();
		            if (couponId != null && couponId != 0) {
		            	ShopUserCoupon couponVo2=new ShopUserCoupon().findById(couponId);
	        		    couponVo2.set("coupon_status", 2);
	        		    couponVo2.set("order_id", orderInfo.getId());
	        		    couponVo2.set("used_time", new Date());
	        		    couponVo2.update();
					}
		            
		            return b1 && b2 && b3 && b4;
		        }        
	             
			});
	        if (!isok) {
	        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "订单提交失败");
			}
	        return ApiBaseAction.toResponsSuccesswithcode(ApiConstant.DONTNEEDPAY,  Kv.by("orderInfo", ret));
		}else {
			//直接处理订单 ,这里就把 优惠券的状态给改变了
			System.out.println("走3");
	        final Ret ret= new Ret();
			boolean isok=Db.tx(new IAtom() {
		        public boolean run()  {	     
		            //写入订单
		        	ShopUser user=new ShopUser().findById(userid);
		            ShopOrder orderInfo = new ShopOrder();
		            String orderSn = RandomStrUtils.generateOrderNumber();
		            orderInfo.setOrderSn(orderSn);
		            orderInfo.setUserId(userid);
		            orderInfo.setCouponId(couponId);
		            orderInfo.setCouponPrice(finalcouponPrice);
		            orderInfo.setAddTime(new Date());
		            orderInfo.setGoodsPrice(finalgoodsTotalPrice);
		            orderInfo.setOrderPrice(orderTotalPrice);
		            orderInfo.setActualPrice(new BigDecimal(0));
		            // 订单已付款
		            orderInfo.setOrderStatus(201);
		            orderInfo.setShippingStatus(0);
		            orderInfo.setPayStatus(2);
		            orderInfo.setShippingId(0);
		            orderInfo.setShippingFee(new BigDecimal(0));
		            orderInfo.setIntegral(0);
		            orderInfo.setIntegralMoney(new BigDecimal(0));
		            orderInfo.setPayName(user.getNickname());
		            orderInfo.setPayTime(new Date());
		            orderInfo.setOrderType("5"); //直接购买
		            boolean b1=orderInfo.save();
		            ret.set("ordersn", orderInfo.getOrderSn());
		            ret.set("goodprice",finalgoodsTotalPrice);
		            ret.set("orderPrice",orderTotalPrice);
		            ret.set("actualPrice",actualPrice);
		            //购买日志
		            ShopConsumeLog log = new ShopConsumeLog();
		            log.setUserId(userid);
		            log.setOrderId(orderSn);
		            log.setCreateTime(DateUtils.getNowDate());
		            log.setUpdateTime(DateUtils.getNowDate());
		            log.setStatus(1);
		            log.setState(0);
		            log.setType(1);
		            log.setBeforeMoney(beforeMoney);
		            log.setAfterMoney(beforeMoney);
		            log.setChangeMoney(0.00);
		            boolean b3 = log.save();
		            
		            //统计商品总价
		            ShopOrderGoods orderGoodsVo = new ShopOrderGoods();
		            orderGoodsVo.setOrderId(orderInfo.getId());
		            orderGoodsVo.setGoodsId(goods.getId());
		            orderGoodsVo.setGoodsSn(goods.getGoodsSn());
		            orderGoodsVo.setProductId(product.getId());
		            orderGoodsVo.setGoodsName(goods.getName());
		            orderGoodsVo.setcategoryId(categoryId);
		            orderGoodsVo.setListPicUrl(goods.getListPicUrl());
		            orderGoodsVo.setMarketPrice(product.getMarketPrice());
		            orderGoodsVo.setRetailPrice(product.getRetailPrice());
		            orderGoodsVo.setNumber(num);
		            orderGoodsVo.setGoodsSpecifitionNameValue(finalgoodsSepcifitionValue);
		            orderGoodsVo.setGoodsSpecifitionIds(product.getGoodsSpecificationIds());	
		            orderGoodsVo.setnumberRemainr(num);
		            boolean b2=orderGoodsVo.save();
		            if (couponId != null && couponId != 0) {
		            	ShopUserCoupon couponVo2=new ShopUserCoupon().findById(couponId);
	        		    couponVo2.set("coupon_status", 2);
	        		    couponVo2.set("order_id", orderInfo.getId());
	        		    couponVo2.set("used_time", new Date());
	        		    couponVo2.update();
					}
		            
		            return b1 && b2 && b3;
		        }        
	             
			});
	        if (!isok) {
	        	return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "订单提交失败");
			}
	        return ApiBaseAction.toResponsSuccesswithcode(ApiConstant.DONTNEEDPAY,  Kv.by("orderInfo", ret));
		}
	
	}*/
	//更具用户选的获取订单价格
	public Ret getPrice(long uid,String specificationIds,int num,Long goodsid,Integer couponId){
		JSONArray array=JSONArray.parseArray(specificationIds);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < array.size(); i++) {
			sb.append(array.getJSONObject(i).get("id"));
			sb.append("_");
		}
		//sb.delete(sb.length()-1,sb.length());
		List<ShopProduct> products=new ShopProduct().find("SELECT * FROM `shop_product` WHERE `goods_specification_ids` = ? AND `goods_id` = ? ",sb.toString(),goodsid);
		if (products.size()>0) {
			int GoodsNumber=products.get(0).getGoodsNumber();
			if (GoodsNumber<num) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "库存不足");
			}
			  BigDecimal goodsTotalPrice;
			  BigDecimal finalgoodsTotalPrice;
			  goodsTotalPrice=products.get(0).getBigDecimal("retail_price").multiply(new BigDecimal(num));

			  BigDecimal goodsMarketPrice;
			  goodsMarketPrice=products.get(0).getBigDecimal("market_price").multiply(new BigDecimal(num));
		      
			  BigDecimal couponPrice = new BigDecimal(0.00);
			  Record couponVo = null;

		        if (couponId != null && couponId != 0) {
		    		SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("couponId", couponId));
		    		couponVo=Db.findFirst(sPara);
		    		
		            if (couponVo != null && couponVo.getInt("coupon_status") == 1) {
		                couponPrice = couponVo.getBigDecimal("type_money");
		            }
		        }
			  //获取是否又可用的优惠券
	          goodsTotalPrice = goodsTotalPrice.subtract(couponPrice);
	          System.out.println("goodsTotalPrice"+goodsTotalPrice);
	          System.out.println("goodsTotalPrice"+goodsTotalPrice.intValue());
			  if (goodsTotalPrice.floatValue()>0) {
				  finalgoodsTotalPrice=goodsTotalPrice;
			  }else {
				  finalgoodsTotalPrice=new BigDecimal(0);
			  }
			  Kv kv=getCouponListByGoods(uid,products.get(0).getId(),num);
			  if (null != kv.get("list") ) {
				  return ApiBaseAction.toResponsSuccess(Kv.by("retail_price", finalgoodsTotalPrice).set("market_price",goodsMarketPrice).set("product",products.get(0).getId()).set("coupon","1"));
			  }
			  return ApiBaseAction.toResponsSuccess(Kv.by("retail_price", finalgoodsTotalPrice).set("market_price",goodsMarketPrice).set("product",products.get(0).getId()).set("coupon","0"));
			 
		}
		return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此商品");
	}
	
	//查询订单状态
	public Ret orderState(String orderId) {
		ShopOrder order=new ShopOrder().findFirst("select pay_status from shop_order where order_sn=?",orderId);
		if (null== order) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此订单");
		}
	 	return ApiBaseAction.toResponsSuccess(order);
	}
	//查询充值状态
	public Ret orderStateByRecharge(String orderId) {
		ShopConsumeLog consumeLog = new ShopConsumeLog().findFirst("select type from shop_consume_log where order_id=?",orderId);
		if (null== consumeLog) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此充值记录");
		}
	 	return ApiBaseAction.toResponsSuccess(consumeLog);
	}
	
	//查询订单商品详情
	public Ret orderGoodsInfo(String orderId) {
		ShopOrderGoods goods=new ShopOrderGoods().findFirst("select og.goods_name,og.goods_specifition_name_value,og.list_pic_url,ca.img_url,og.number from shop_order_goods og left join shop_order o on og.order_id=o.id left join shop_category ca on og.category_id = ca.id  where o.order_sn=?",orderId);
		 return ApiBaseAction.toResponsSuccess(goods);
	}
	//查询订单商品详情
	public Ret orderParentGoodsInfo(String orderId) {
		ShopOrderGoods goods=new ShopOrderGoods().findFirst("select og.goods_name,og.goods_specifition_name_value,og.list_pic_url,og.number from shop_order_goods og left join shop_order o on og.order_id=o.id left join shop_order_reciver ore on ore.parent_id= o.id where ore.order_sn=?",orderId);
		 return ApiBaseAction.toResponsSuccess(goods);
	}
	//获取所有祝福
	public Ret getAllBlessing() {
		List<ShopBlessing> sList=new ShopBlessing().find("select * from shop_blessing  ORDER BY RAND() LIMIT 10;");
		return ApiBaseAction.toResponsSuccess(sList);		
	}	
	//赠送
	public Ret sendToOthers(String sender_name,String receiver_name,String orderId,String blessingname ) {
		ShopOrder order=new ShopOrder().findFirst("select * from shop_order where order_sn=?",orderId);
		if (null== order) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此订单");
		}
		order.setSenderName(sender_name);
		order.setReceiverName(receiver_name);
		order.set("blessingname",blessingname);
		order.update();
		
		return ApiBaseAction.toResponsSuccess(Kv.by("orderId", orderId));	
	}
	//获取订单详情
	public Ret getOrderDetail(String OrderId) {
		ShopOrder order=new ShopOrder().findFirst("select o.order_sn,o.receiver_name,o.sender_name,o.blessingname,o.add_time,c.img_url,c.video_img_url,c.video_url from shop_order o left join shop_order_goods og on o.id=og.order_id left join shop_category c on c.id=og.category_id where o.order_sn = ?",OrderId);
		order.put("add_time", DateUtils.format(order.getAddTime(), "yyyy-MM-dd"));
		return ApiBaseAction.toResponsSuccess(Kv.by("orderDetail", order));
	}
	
	//接收人打开显示状态是否已被领取
	public Ret getReceive(String OrderId) {
		//返回给前端的信息
		ShopOrder order1 = new ShopOrder().findFirst("select o.order_sn,o.receiver_name,o.sender_name,o.blessingname,o.add_time,c.img_url,c.video_img_url,c.video_url from shop_order o left join shop_order_goods og on o.id=og.order_id left join shop_category c on c.id=og.category_id where o.order_sn = ?",OrderId);
		order1.put("add_time", DateUtils.format(order1.getAddTime(), "yyyy-MM-dd"));
		//判断是否已被领取
		ShopOrder receive=new ShopOrder().findFirst("select o.*,og.number_remain,og.number from shop_order o left join shop_order_goods og on o.id=og.order_id  where order_sn=?",OrderId);
		if (receive.getInt("number") == 0 || receive.getInt("number_remain") == 0) {
			//已被领取
			order1.put("is_receive", 1);
		}else {
			//未领取
			order1.put("is_receive", 0);
		}
		return ApiBaseAction.toResponsSuccess(Kv.by("orderDetail", order1));
	}
	
	//处理充值的支付结果
	/*public Ret rechargePay(String orderSn,String result_code) {
		if ("SUCCESS".equals(result_code)) {
			Db.tx(new IAtom() {
		        public boolean run()  {	     
		            //更改收付日志
		            ShopConsumeLog log = new ShopConsumeLog().findFirst("select * from shop_consume_log where order_id = ?",orderSn);
		            log.setType(1);
		            log.setUpdateTime(DateUtils.getNowDate());
		            boolean b2 = log.update();
					return b2;
		        }        
			});
		  return Ret.ok();
		}else {
			log.error("订单" + orderSn + "充值失败");
			 return Ret.ok();
		}
	}*/
	//处理订单支付结果
	/*public Ret dealPay(String orderSn,String result_code) {
		if ("SUCCESS".equals(result_code)) {
			
			Db.tx(new IAtom() {
		        public boolean run()  {	     
		            //写入订单
		        	ShopOrder order=new ShopOrder().findFirst("select * from shop_order where order_sn=?",orderSn);
		        	if(null == order) {
		        		//更改收付日志 改变用户余额
			            ShopConsumeLog log = new ShopConsumeLog().findFirst("select * from shop_consume_log where order_id = ?",orderSn);
			            log.setType(1);
			            log.setUpdateTime(DateUtils.getNowDate());
			            boolean b2 = log.update();
			            int userId = log.getUserId();
			            double afterMoney = log.getAfterMoney();
			            ShopUser user = new ShopUser().findFirst("select * from shop_user where id = ?",userId);
			            if (null != user) {
							user.setMoney(afterMoney);
							boolean b3 = user.update();
							return b2 && b3;
						}else {
							return false;
						}
		        	}else {
		        		log.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		        		//更新用户钱包
		        		ShopConsumeLog log = new ShopConsumeLog().findFirst("select * from shop_consume_log where order_id = ?",orderSn);
			            log.setType(1);
			            boolean b5 = log.update();
			        	int userId = order.getUserId();
			        	ShopUser user = new ShopUser().findFirst("select * from shop_user where id = ?",userId);
			        	if (null == user) {
							return false;
						}
			        	user.setMoney(0.00);
			            boolean b4 = user.update();
			            // 订单已付款
			        	order.setOrderStatus(201);
			        	order.setShippingStatus(0);
			            order.setPayStatus(2);
			            boolean b1=order.update();        
			            if (order.getCouponId() != 0) {
			        		SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("couponId", order.getCouponId()));
			        		  Record couponVo2=Db.findFirst(sPara);
			        		  Record coupon = new Record();
			        		  coupon.set("id", couponVo2.getInt("user_coupon_id"));
			        		  coupon.set("coupon_status", 2);
			        		  coupon.set("order_id", order.getId());
			        		  coupon.set("used_time", new Date());
			        		 Db.update("shop_user_coupon", coupon);
						}
						return b1 && b4 && b5;
					}
		        }
			});
		  return Ret.ok();
		}else {
			log.error("订单" + orderSn + "支付失败");
			 return Ret.ok();
		}
	}*/
	public Ret accept(Integer uid,String orderId,String userName,String telNumber,String cityName,String provinceName,String countyName,String detailInfo,String reamak,String postalCode) {
		ReentrantLock lock=new ReentrantLock();
		try {
		
			lock.lock();
			ShopOrder order=new ShopOrder().findFirst("select o.*,og.number_remain,og.number from shop_order o left join shop_order_goods og on o.id=og.order_id  where order_sn=?",orderId);
			if (order == null  ) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此订单");
			}
			if (order.getInt("number") == 0 || order.getInt("number_remain") == 0) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "商品已被抢完");
			}
			Db.tx(new IAtom() {
				public boolean run() throws SQLException {
					// 先删除 news_feed
					Db.update("update shop_order_goods set number_remain = number_remain- 1 where order_id=?",order.getId());
					ShopOrderReciver orderReciver=new ShopOrderReciver();
					orderReciver.setParentId(order.getId());
					orderReciver.setConsignee(userName);
					orderReciver.setMobile(telNumber);
					orderReciver.setAddress(detailInfo);
					orderReciver.setProvince(provinceName);
					orderReciver.setCity(cityName);
					orderReciver.setDistrict(countyName);
					orderReciver.setAddress(detailInfo);
					orderReciver.setOrderStatus(202L);
					orderReciver.setReceiverId(uid);
					orderReciver.setPostscript(reamak);
					orderReciver.setAddTime(new Date());
					orderReciver.setCountry(postalCode);
					orderReciver.setNumber(1);
					orderReciver.setIsLocation(1);
					orderReciver.setOrderSn(RandomStrUtils.generateOrderNumber());
					// 再删除 feedback_reply
					boolean b1=orderReciver.save();
					ShopOrderLog loge=new ShopOrderLog();
					loge.setCreateTime(new Date());
					loge.setOrderId(order.getId());
					loge.setOrderReciverId(orderReciver.getId());
					loge.setDetail(userName+"收下礼品1份");
					loge.save();
				
					return b1;
				}
			});
			  
			return ApiBaseAction.toResponsSuccess(Kv.by("adddress", provinceName+cityName+countyName+detailInfo).set("name",userName));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, e.getMessage());
		}finally {
			MessageService.init();
			lock.unlock();
		}
	}
	//赠送他人
	public Ret otheraccept(String orderId,Integer uid) {
		
		//先去查询接收是否有这个订单号。
		ShopOrderReciver orderReciver=new ShopOrderReciver().findFirst("SELECT * FROM `shop_order_reciver` WHERE `order_sn` = ? AND `receiver_id` = ? ",orderId,uid);
		if (orderReciver !=null && orderReciver.getConsignee()!= null) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "请勿重复领取");
		}
		ReentrantLock lock=new ReentrantLock();
		try {
		
			lock.lock();
			ShopOrder order=new ShopOrder().findFirst("select o.*,og.number_remain,og.number from shop_order o left join shop_order_goods og on o.id=og.order_id  where order_sn=?",orderId);
			if (order == null  ) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "查无此订单");
			}
			if (order.getInt("number") == 0 || order.getInt("number_remain") == 0) {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "此礼卡已被领取");
			}	
			Ret ret=new Ret();
			Db.tx(new IAtom() {
				public boolean run() throws SQLException {
					// 先删除 先把订单数减一
					Db.update("update shop_order_goods set number_remain = number_remain- 1 where order_id=?",order.getId());
					ShopOrderReciver orderReciver=new ShopOrderReciver();
					orderReciver.setReceiverId(uid);
					orderReciver.setParentId(order.getId());
					orderReciver.setOrderSn(RandomStrUtils.generateOrderNumber());
					orderReciver.setNumber(1);
					// 再删除 feedback_reply
					ShopUser user=new ShopUser().findById(uid);
					boolean b1=orderReciver.save();
					ret.set("orderSn", orderReciver.getOrderSn());
					ShopOrderLog loge=new ShopOrderLog();
					loge.setCreateTime(new Date());
					loge.setOrderId(order.getId());
					loge.setDetail(user.getNickname()+"收下礼品1份");
					loge.setOrderReciverId(orderReciver.getId());
					loge.save();
					return b1;
				}
			});
			return ApiBaseAction.toResponsSuccess(Kv.by("oderId", ret.get("orderSn")));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, e.getMessage());
		}finally {
			MessageService.init();
			lock.unlock();
		}
	}
	public Ret otherAccpetUpdate(Integer uid,String orderId,String userName,String telNumber,String cityName,String provinceName,String countyName,String detailInfo,String reamak,String postalCode) {
		ShopOrderReciver orderReciver=new ShopOrderReciver().findFirst("SELECT * FROM `shop_order_reciver` WHERE `order_sn` = ? AND `receiver_id` = ? ",orderId,uid);
		if (orderReciver !=null && orderReciver.getConsignee()!= null) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "请勿重复领取");
		}
		
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除 news_feed

				orderReciver.setConsignee(userName);
				orderReciver.setMobile(telNumber);
				orderReciver.setAddress(detailInfo);
				orderReciver.setProvince(provinceName);
				orderReciver.setCity(cityName);
				orderReciver.setDistrict(countyName);
				orderReciver.setAddress(detailInfo);
				orderReciver.setReceiverId(uid);
				orderReciver.setPostscript(reamak);
				orderReciver.setNumber(1);
				orderReciver.setCountry(postalCode);
				orderReciver.setIsLocation(1);
				// 再删除 feedback_reply
				boolean b1=orderReciver.update();

				return b1;
			}
		});
		  
		return ApiBaseAction.toResponsSuccess(Kv.by("adddress", provinceName+cityName+countyName+detailInfo).set("name",userName));
	}
	
	//补充填写地址
	public Ret resetAddress(Integer uid,String orderId,String userName,String telNumber,String cityName,String provinceName,String countyName,String detailInfo,String reamak,String postalCode) {
		ShopOrderReciver orderReciver=new ShopOrderReciver().findFirst("SELECT * FROM `shop_order_reciver` WHERE `order_sn` = ? AND `receiver_id` = ? ",orderId,uid);
		if (null ==orderReciver) {
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "找不到礼卡");
		}
			orderReciver.setConsignee(userName);
			orderReciver.setMobile(telNumber);
			orderReciver.setAddress(detailInfo);
			orderReciver.setProvince(provinceName);
			orderReciver.setCity(cityName);
			orderReciver.setDistrict(countyName);
			orderReciver.setAddress(detailInfo);
			orderReciver.setPostscript(reamak);
			orderReciver.setCountry(postalCode);
			orderReciver.setIsLocation(1);
			boolean b1=orderReciver.update();
			if(b1) {
				return ApiBaseAction.toResponsSuccess(Kv.by("adddress", provinceName+cityName+countyName+detailInfo).set("name",userName));
			}else {
				return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "填写收货信息失败,请联系管理员");
			}
			
	}
	
	// 获取收到的礼卡
	public Ret getOrderList(Integer uid,int page,int limit) {
		SqlPara sPara=Db.getSqlPara("order.queryApiOrderList",uid);
		Page<Record> records=Db.paginate(page,limit,sPara);
		log.info("getOrderList"+records.getList());
		return ApiBaseAction.toResponsSuccess(Kv.by("data", records));
	}
	
	
	// 获取收到的礼卡
	public Ret getOrderReciverList(Integer uid,int page,int limit) {
		SqlPara sPara=Db.getSqlPara("order.queryApiOrderReciverList",uid);
		Page<Record> records=Db.paginate(page,limit,sPara);
		log.info("getOrderReciverList"+records.getList());
		return ApiBaseAction.toResponsSuccess(Kv.by("data", records));
	}
	//获取用户优惠券列表
	public Ret getPageCounList(int page,int limit,Integer uid) {
		SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("user_id", uid));
		Page<Record> records=Db.paginate(page, limit, sPara);
        for (Record couponVo : records.getList()) {
            if (couponVo.getInt("coupon_status")==1) {
                // 检查是否过期
                if(couponVo.getDate("use_end_date").before(new Date())) {
                    couponVo.set("coupon_status",3);
                    Db.update("update shop_user_coupon set coupon_status=3 where id = ?", couponVo.getInt("user_coupon_id"));
                }
            }
            if (couponVo.getInt("coupon_status")==3) {
                // 检查是否不过期
                if(couponVo.getDate("use_end_date").after(new Date())) {
                	couponVo.set("coupon_status",1); 
                	Db.update("update shop_user_coupon set coupon_status=1 where id = ?", couponVo.getInt("user_coupon_id"));
                }
            }
        }
		return ApiBaseAction.toResponsSuccess(Kv.by("data", records));
	}
	//获取购买订单详情
	public Ret getOrderDetailInfo(String orderid) {
		Kv kv=new Kv();
		SqlPara sPara=Db.getSqlPara("order.queryApiObject", orderid);
		Record records=Db.findFirst(sPara);
		kv.set("orderDetail",records);
		List<ShopOrderLog> list=new ShopOrderLog().find("select detail,create_time from shop_order_log where order_id=? order by create_time desc",records.getInt("id"));
		kv.set("orderLogList",list);
		return ApiBaseAction.toResponsSuccess(Kv.by("data", kv));
	}
	
	//获取接收订单详情
	public Ret getOrderReciverDetailInfo(String orderid) {
		Kv kv=new Kv();
		SqlPara sPara=Db.getSqlPara("order.queryApiReciverObject", orderid);
		
		Record records=Db.findFirst(sPara);
		
		kv.set("orderDetail",records);
		return ApiBaseAction.toResponsSuccess(Kv.by("data", kv));
	}
	
	public Ret getTrack(String orderId) throws Exception {
		return new OrderGoodsService().track(orderId);
	}
	//兑换优惠券
	public Ret exchange(String code,Integer uid) {
		ReentrantLock lock=new ReentrantLock();
		try {
			
			lock.lock();
			SqlPara sPara=Db.getSqlPara("coupon.queryUserCoupons",Kv.by("source_key", code));
		
			Record couponVo=Db.findFirst(sPara);
			if (couponVo == null ) {
				  return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, " 查无此优惠券");
			}
	        if (couponVo.getInt("user_id") !=0) {
	            // 检查是否过期
	        	  return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "该优惠券已经被领取");
	        }
	        if (couponVo.getInt("coupon_status") !=1) {
	            // 检查是否过期
	        	  return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "该优惠券已经使用");
	        }
	        if(couponVo.getDate("use_end_date").before(new Date())) {
//	            couponVo.set("coupon_status",3);
//	            Db.update("update shop_user_coupon set coupon_status=3 where id=?", couponVo.getInt("user_coupon_id"));
	            return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, "该优惠券已经过期");
	        }
	        Db.update("update shop_user_coupon set user_id=? , add_time = ? where id=?",uid,new Date(), couponVo.getInt("user_coupon_id"));;
	        return ApiBaseAction.toResponsSuccess(couponVo);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return ApiBaseAction.toResponsFail(ApiConstant.CODE_FAIL, e.getMessage());
			// TODO: handle exception
		}finally {
			lock.unlock();
		}

	}
	//获取我的页面的初始信息
	public Ret GetMyInfo(int userid) { 
		ShopUser user=new ShopUser().findFirst("select avatar,nickname,id,money from shop_user where id=?",userid);
		ShopOrderReciver orderReciver=new ShopOrderReciver().findFirst("select count(id) as recivenum from shop_order_reciver where `receiver_id`=?",userid);
		ShopOrder orderbuy=new ShopOrder().findFirst("select count(id) as buynum from shop_order where `user_id`= ? and `order_status` != 0 ",userid);
		ShopUserCoupon userCoupon=new ShopUserCoupon().findFirst("select count(id) as counponnum from shop_user_coupon where `user_id`= ? ",userid);
		user.put("recivenum",orderReciver.get("recivenum"));
		user.put("buynum",orderbuy.get("buynum"));
		user.put("counponnum",userCoupon.get("counponnum"));
		return ApiBaseAction.toResponsSuccess(user);
	}
	
}
