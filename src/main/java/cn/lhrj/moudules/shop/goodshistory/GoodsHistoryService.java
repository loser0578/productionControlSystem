package cn.lhrj.moudules.shop.goodshistory;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCategory;

public class GoodsHistoryService {
    
	public static final GoodsHistoryService me = new GoodsHistoryService();
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("shipping.queryList", kv);
		List<Record> records=Db.find(sPara);
	
        //添加顶级菜单
		Record menu=new Record();
		menu.set("id", 0L);
		menu.set("name","一级菜单");
		menu.set("parent_id",-1L);
		menu.set("open",true);
		records.add(menu);
		return records;
	}
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("category.queryObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	public void save(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.save();
	}
	public void update(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_category",array.get(i));
		}
	}
}
