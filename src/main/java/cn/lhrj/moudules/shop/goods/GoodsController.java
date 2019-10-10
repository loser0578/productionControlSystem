package cn.lhrj.moudules.shop.goods;

import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopGoods;
import cn.lhrj.component.base.BaseProjectController;

public class GoodsController extends BaseProjectController{
	
	GoodsService srv=GoodsService.me;
	
	public void index() {
		render("/page/shop/goods.html");
	}
	public void goodshistory() {
		render("/page/shop/goodshistory.html");
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
		renderJson(Ret.ok("goods", srv.queryObject(getParaToLong())));
	}
    /**
     * 查看所有列表
     */	
	public void queryAll() {
		renderJson(Ret.ok("list", srv.selectL1()));
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
		srv.delectByIds(HttpKit.readData(getRequest()),getLoginAccount().getId());
		renderJson(Ret.ok());
	}
    /*
     *
	  * 下架
	 */	
	public void unSale() {
		renderJson(srv.unSale(Integer.parseInt(HttpKit.readData(getRequest())),getLoginAccount().getId()));
	}
    /**
          * 上架
     */
	public void enSale() {
		renderJson(srv.enSale(Integer.parseInt(HttpKit.readData(getRequest())),getLoginAccount().getId()));
	}	
    /**
          * 商品回收站
     *
     * @param params
     * @return
     */
	public void historyList(@Para("") ShopGoods model) {
		Kv para=new Kv();
		if (model._getAttrValues().length != 0) {
			para.set("name", model.get("name"));
		}
		para.set("isDelete",1);
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	
    /**
     * 商品从回收站恢复
     */	
	public void back() {
		srv.back(HttpKit.readData(getRequest()), getLoginAccount().getId());
		renderJson(Ret.ok());
	}
	
}
