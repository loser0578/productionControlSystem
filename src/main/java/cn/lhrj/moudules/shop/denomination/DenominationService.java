package cn.lhrj.moudules.shop.denomination;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopDenomination;
import cn.lhrj.common.utils.DateUtils;


public class DenominationService {
public static final DenominationService me = new DenominationService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("denomination.list", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryList() {
		SqlPara sPara=Db.getSqlPara("denomination.queryList");
		return Db.find(sPara);
	}
	
	/**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("denomination.queryObject", menuid);
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
		ShopDenomination denomination=FastJson.getJson().parse(string, ShopDenomination.class);
		denomination.set("create_time",DateUtils.getNowDate());
		denomination.set("_status",1);
		denomination.save();
	}
	public void update(String string) {
		ShopDenomination denomination=FastJson.getJson().parse(string, ShopDenomination.class);
		denomination.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_denomination",array.get(i));
		}
	}
}
