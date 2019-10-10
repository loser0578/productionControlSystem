package cn.lhrj.moudules.shop.goodsgallery;


import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import cn.lhrj.component.base.BaseProjectController;

public class GoodsGalleryController extends BaseProjectController{
	
	GoodsGalleryService srv=GoodsGalleryService.me;
	
	public void index() {
		render("/page/shop/goods.html");
	}

	public void queryAll() {
		Kv para=new Kv();

		para.set("goodsId", getPara("goods_id"));

		renderJson(Ret.ok("list", srv.queryList(para)));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("goods", srv.queryObject(getParaToLong())));
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
