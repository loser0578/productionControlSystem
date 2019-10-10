package cn.lhrj.moudules.shop.shipping;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import cn.lhrj.component.base.BaseProjectController;

public class ShippingController extends BaseProjectController{
	
	ShippingService srv=ShippingService.me;
	
    /**
     * 查看所有列表
     */
	public void queryAll() {
		Kv para = Kv.by("name", null).set("parent_id", null).set("sidx", null).set("offset",null);
		List<Record> records=srv.queryAll(para);
		renderJson(Ret.ok("list", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("category", srv.queryObject(getParaToLong())));
	}
	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void getCategorySelect() {
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
}
