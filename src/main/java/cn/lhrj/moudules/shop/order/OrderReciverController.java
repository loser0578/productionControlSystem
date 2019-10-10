package cn.lhrj.moudules.shop.order;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.component.base.BaseProjectController;

public class OrderReciverController extends BaseProjectController{
	@Inject
	OrderReciverService srv;
	
	public void index() {
		render("/page/shop/orderReciver.html");
	}
	public void list() {
		Kv para=new Kv();
		ShopOrder model=getModel(ShopOrder.class,"",true);
		if (model._getAttrValues().length != 0) {
			para.set("orderStatus", model.get("order_status"));
			para.set("orderType", model.get("order_type"));
			para.set("orderSn", model.get("order_sn"));
			para.set("orderStatus", model.get("order_status"));

		}
		para.set("isDelete",0);
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("order", srv.queryObject(getParaToLong()).set("code", 1)));
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
          * 发货
     *
     * @param order
     * @return
     */	
	public void sendGoods() {
		renderJson(srv.sendGoods(HttpKit.readData(getRequest())));
	}
	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void getGoodsSelect() {
		renderJson(Ret.ok("list", srv.select()));
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

	
	

}
