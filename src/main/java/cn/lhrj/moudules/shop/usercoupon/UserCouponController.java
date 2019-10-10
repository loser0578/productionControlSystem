package cn.lhrj.moudules.shop.usercoupon;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopUserCoupon;
import cn.lhrj.component.base.BaseProjectController;

public class UserCouponController extends BaseProjectController{
	
	UserCouponService srv=UserCouponService.me;
	
	public void index() {
		render("/page/shop/usercoupon.html");
	}
	public void list() {
		Kv para=new Kv();
		ShopUserCoupon model=getModel(ShopUserCoupon.class,"",true);
		if (model._getAttrValues().length != 0) {
			para.set("userName", model.get("user_name"));
			para.set("couponName", model.get("coupon_name"));
			para.set("userId", model.get("user_id"));
		}

		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("coupon", srv.queryObject(getParaToLong())));
	}
    /**
     * 确定收货
     *
     * @param id
     * @return
     */
	public void confirm() {
	
		
		renderJson(srv.confirm(HttpKit.readData(getRequest())));
	
	}
    /**
     * 保存
     */	
	public void save() {
		srv.save(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 修改
     */
	public void update() {	
		srv.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 删除
     */	
	public void delete() {	
		srv.delectByIds(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 按用户、商品下发优惠券
     *
     * @param params
     * @return
     */
	public void publish() {
		srv.publish(HttpKit.readData(getRequest()));
		renderJson(1);
	}
}
