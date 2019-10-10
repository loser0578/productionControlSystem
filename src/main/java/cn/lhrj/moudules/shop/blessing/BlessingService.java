package cn.lhrj.moudules.shop.blessing;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopBlessing;

public class BlessingService {
    
	public static final BlessingService me = new BlessingService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("blessing.list", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryList() {
		SqlPara sPara=Db.getSqlPara("blessing.queryList");
		return Db.find(sPara);
	}
	
	
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("blessing.queryObject", menuid);
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
		ShopBlessing blessing=FastJson.getJson().parse(string, ShopBlessing.class);
		blessing.save();
	}
	public void update(String string) {
		ShopBlessing blessing=FastJson.getJson().parse(string, ShopBlessing.class);
		blessing.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_blessing",array.get(i));
		}
	}
}
