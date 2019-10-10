package cn.lhrj.moudules.shop.goodsspecification;

import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.component.base.BaseProjectController;

public class GoodsSpecificationController extends BaseProjectController{
	
	GoodsSpecificationService srv=GoodsSpecificationService.me;
	
	public void index() {
		render("/page/shop/goodsspecification.html");
	}
	public void list(@Para("name") String goodsname) {
		
		Kv para=new Kv().set("goodsName", goodsname);
		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	
	
	public void queryAll(@Para("specification_id") Integer specificationId,@Para("goods_id") Integer goodsId) {
		Kv para=new Kv();
		if (null != specificationId) {
			para.set("specificationId",specificationId);
		}
		if (null != goodsId) {
			para.set("goodsId",goodsId);
		}
		renderJson(Ret.ok("list", srv.queryAll(para)));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("goodsSpecification", srv.queryObject(getParaToLong())));
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
	
	public void getSpecificationId(@Para("specification_id") Integer specificationId) {

		renderJson(srv.findByID(specificationId));

	}
}
