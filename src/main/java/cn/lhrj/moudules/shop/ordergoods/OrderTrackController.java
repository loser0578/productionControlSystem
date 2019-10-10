package cn.lhrj.moudules.shop.ordergoods;

import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopGoods;
import cn.lhrj.component.base.BaseProjectController;

public class OrderTrackController extends BaseProjectController{
	
	OrderGoodsService srv=OrderGoodsService.me;
	public void index() {

		set("orderId", getPara("orderId"));
		render("/page/shop/orderTrack.html");
	}
	
	public void list(@Para("") ShopGoods model) {
		
		Kv para=new Kv();
		if (model._getAttrValues().length != 0) {
			para.set("name", model.get("name"));

		}
		para.set("isDelete",0);
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("order", srv.queryObject(getParaToLong())));
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
    /**
     * 删除
     */	
	public void delete() {	
		srv.delectByIds(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
	
	public void queryAll() {
		Kv para=new Kv();
		renderJson("list", srv.queryAll(para.set("orderId",getPara("orderId"))));
	}
	public void track() throws Exception {
		renderJson(srv.trackAdmin(getParaToInt("orderId")));
	}
}
