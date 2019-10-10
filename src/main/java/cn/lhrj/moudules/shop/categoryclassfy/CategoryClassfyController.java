package cn.lhrj.moudules.shop.categoryclassfy;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopCategoryclassfy;
import cn.lhrj.component.base.BaseProjectController;

public class CategoryClassfyController extends BaseProjectController{
	
	CategoryClassfyService srv=CategoryClassfyService.me;
	
	public void index() {
		render("/page/shop/categoryclassfy.html");
	}
	
	public  void list() {
		ShopCategoryclassfy model=getModel(ShopCategoryclassfy.class, "", true);
		Kv para=new Kv();
		if (model._getAttrValues().length != 0) {
			para.set("name", model.get("name"));
		}
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	
	
	public void queryAll() {
		Kv para = Kv.by("name", null).set("parent_id", null).set("sidx", null).set("offset",null);
		List<Record> records=srv.queryAll(para);
		renderJson(Ret.ok("list", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("categoryclassify", srv.queryObject(getParaToLong())));
	}
	//获取栏目
	public void getCategoryclassfy() {

		renderJson(Ret.ok("list", srv.select(getPara() == null ?  0 : getParaToInt())));
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
