package cn.lhrj.moudules.shop.product;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopProduct;
import cn.lhrj.component.base.BaseProjectController;

public class ShopProductController extends BaseProjectController{
	
	ShopProductService srv=ShopProductService.me;
	
	public void index() {
		render("/page/shop/product.html");
	}
	public void list() {
		Kv para=new Kv();
		ShopProduct model=getModel(ShopProduct.class,"",true);
		if (model._getAttrValues().length != 0) {
			para.set("goodsId", model.get("goods_id"));
		}
		if (getPara("goodsName") !=null) {
			para.set("goodsName",getPara("goodsName"));
		}
		para.set("isDelete",0);
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("product", srv.queryObject(getParaToLong())));
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
    /**
     * 删除
     */	
	public void delete() {	
		srv.delectByIds(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
}
