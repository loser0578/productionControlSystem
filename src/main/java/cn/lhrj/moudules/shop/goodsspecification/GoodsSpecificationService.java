package cn.lhrj.moudules.shop.goodsspecification;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopGoodsSpecification;
import cn.lhrj.common.model.ShopShipping;

public class GoodsSpecificationService {
    
	public static final GoodsSpecificationService me = new GoodsSpecificationService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("goodsspecification.queryList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("goodsspecification.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("goodsspecification.queryObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("menu.queryNotButtonList");
		List<Record> records=Db.find(sPara);
        //添加顶级菜单
		Record menu=new Record();
		menu.set("menu_id", 0L);
		menu.set("name","一级菜单");
		menu.set("parent_id",-1L);
		menu.set("open",true);
		records.add(menu);
		return records;
	}
	
	public void save(String string) {
		ShopGoodsSpecification shopGoodsSpecification=FastJson.getJson().parse(string, ShopGoodsSpecification.class);

		shopGoodsSpecification.save();
	}
	public void update(String string) {
		ShopGoodsSpecification shopGoodsSpecification=FastJson.getJson().parse(string, ShopGoodsSpecification.class);

		shopGoodsSpecification.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_goods_specification",array.get(i));
		}
	}


	public Ret findByID(int idValue) {
		ShopGoodsSpecification specification=new ShopGoodsSpecification().findFirst("select specification_id from shop_goods_specification where id= ?",idValue);
		List<ShopShipping> sList=new ShopShipping().find("select id from shop_specification order by sort_order");
		int j=0;
		for (int i = 0; i < sList.size(); i++) {
			/*if (sList.get(i).getId()==specification.getSpecificationId()) {
				 j=i;
				 break;
			}*/
		}
		
		return Ret.ok("data",j);
	}


}
