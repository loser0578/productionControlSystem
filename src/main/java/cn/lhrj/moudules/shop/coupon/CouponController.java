package cn.lhrj.moudules.shop.coupon;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopUser;
import cn.lhrj.component.base.BaseProjectController;

public class CouponController extends BaseProjectController{
	
	CouponService srv=CouponService.me;
	
	public void index() {
		render("/page/shop/coupon.html");
	}
	public void list() {
		Kv para=new Kv();
		ShopUser model=getModel(ShopUser.class,"",true);
		if (model._getAttrValues().length != 0) {
			para.set("orderStatus", model.get("order_status"));
			para.set("orderType", model.get("order_type"));
			para.set("orderSn", model.get("order_sn"));
			para.set("orderStatus", model.get("order_status"));

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
	
		
		//renderJson(srv.confirm(HttpKit.readData(getRequest())));
	
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
