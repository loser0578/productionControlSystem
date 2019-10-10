package cn.lhrj.moudules.shop.blessing;

import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopBlessing;
import cn.lhrj.component.base.BaseProjectController;

public class BlessingController extends BaseProjectController{
	
	BlessingService srv=BlessingService.me;
	
	public void index() {
		render("/page/shop/blessing.html");
	}
	public void list(@Para("") ShopBlessing model) {
		
		Kv para=new Kv();
		if (model._getAttrValues().length != 0) {
			para.set("name", model.get("name"));
		}
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
    /**
     * 查看所有列表
     */	
	public void queryAll() {
		renderJson(Ret.ok("list",srv.queryList()));
	}
	
	
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("blessing", srv.queryObject(getParaToLong())));
	}
	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void select() {
		renderJson(Ret.ok("menuList", srv.select()));
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
