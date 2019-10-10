package cn.lhrj.moudules.shop.specification;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopSpecification;

public class SpecificationService {
    
	public static final SpecificationService me = new SpecificationService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("specification.list", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	

	public List<Record> queryList() {



		
		SqlPara sPara=Db.getSqlPara("specification.queryList");
		return Db.find(sPara);
	}
	
	
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("specification.queryObject", menuid);
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
	
	public void save(String shopSpecification) {
		ShopSpecification specification2=FastJson.getJson().parse(shopSpecification, ShopSpecification.class);
		specification2.save();
	}
	public void update(String shopSpecification) {
		ShopSpecification specification2=FastJson.getJson().parse(shopSpecification, ShopSpecification.class);
		specification2.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_specification",array.get(i));
		}
	}
}
