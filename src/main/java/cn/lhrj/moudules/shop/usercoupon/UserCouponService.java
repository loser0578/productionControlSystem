package cn.lhrj.moudules.shop.usercoupon;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCoupon;
import cn.lhrj.common.model.ShopCouponGoods;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopShipping;
import cn.lhrj.common.model.ShopUserCoupon;

public class UserCouponService {
    
	public static final UserCouponService me = new UserCouponService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("usercoupon.queryList", kv);
		
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
		List<Record> records=Db.find(sPara);
	
        //添加顶级菜单
		Record menu=new Record();
		menu.set("id", 0L);
		menu.set("name","一级菜单");
		menu.set("parent_id",-1L);
		menu.set("open",true);
		records.add(menu);
		return records;
	}
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
	
		SqlPara sPara=Db.getSqlPara("coupon.queryObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 确定收货
     *
     * @param id
     * @return
     */	
	public Ret confirm(String id) {
		ShopOrder order=new ShopOrder().findById(Integer.parseInt(id));
		Long shippingStatus = order.getShippingStatus();//发货状态
	    Long payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
            return Ret.fail("msg","此订单未付款，不能确认收货！").set("code", 500);
        }
        if (4 == shippingStatus) {
        	 return Ret.fail("msg","此订单处于退货状态，不能确认收货！").set("code", 500);
        }
        if (0 == shippingStatus) {
        	return Ret.fail("msg","此订单未发货，不能确认收货！").set("code", 500);
        }
        order.setShippingStatus(2L);
        order.setOrderStatus(301L);
        order.update();
        return Ret.ok();
	}
    /**
          * 发货
     *
     * @param order
     * @return
     */
	public Ret sendGoods(String string){
		ShopOrder order2=FastJson.getJson().parse(string, ShopOrder.class);
		ShopOrder order=new ShopOrder().findById(order2.getId());
	    Long payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
        	 return Ret.fail("msg","此订单未付款！").set("code", 500);
        }
        ShopShipping shipping=new ShopShipping().findById(order.getShippingId());
        if (null != shipping) {
            order.setShippingName(shipping.getName());
        }
        order.setOrderStatus(300L);//订单已发货
        order.setShippingStatus(1L);//已发货
        return Ret.ok();
	}
	
	
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("goods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	public void save(String string) {
		ShopCoupon shopCoupon=FastJson.getJson().parse(string, ShopCoupon.class);
		shopCoupon.save();
	}
	public void update(String string) {
		ShopCoupon shopCoupon=FastJson.getJson().parse(string, ShopCoupon.class);
		shopCoupon.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_user_coupon",array.get(i));
		}
	}
	public Ret publish(String string) {
		PublishModel pModel=FastJson.getJson().parse(string, PublishModel.class);
		Integer sendType=pModel.getSendType();
		Integer couponId=pModel.getCouponId();
		 //是否发送短信通知
        boolean sendSms = "true".equals(pModel.isSendSms());
        if (null == sendType) {
       	 return Ret.fail("msg","发放方式不能为空").set("code", 500);
        }
        if (null == couponId) {
        	 return Ret.fail("msg","优惠券不能为空").set("code", 500);
        }
        if (1==sendType) { // 下发用户逗号分割
			String userIds=pModel.getUserIds();
            if (StrKit.isBlank(userIds)) {
           	 return Ret.fail("msg","(用户不能为空").set("code", 500);
            }
            for(String strUserId:userIds.split(",")) {
            	if (StrKit.isBlank(strUserId)) {
					continue;
				}
            	Long userId = Long.valueOf(strUserId);
            	ShopUserCoupon shopUserCoupon=new ShopUserCoupon();
            	shopUserCoupon.setUserId(userId);
            	shopUserCoupon.setCouponId(couponId);
            	shopUserCoupon.setCouponNumber("1");
            	shopUserCoupon.setAddTime(new Date());
            	shopUserCoupon.save();
            	if (sendSms) {
            		   // todo 发送短信
				}
            	
            }
		}else if (3 == sendType) {
			String goodsIds=pModel.getGoodsIds();
			if (StrKit.isBlank(goodsIds)) {
				return Ret.fail("msg", "商品ID不能为空").set("code", 500);
			}
            for (String goodsId : goodsIds.split(",")) { // 下发商品逗号分割
                if (StrKit.isBlank(goodsId)) {
                    continue;
                }
                ShopCouponGoods couponGoodsVo = new ShopCouponGoods();
                couponGoodsVo.setCouponId(couponId);
                couponGoodsVo.setGoodsId(Integer.valueOf(goodsId));
                couponGoodsVo.save();
            }
		}
        //todo
		return Ret.ok();
		
	}
	
}
