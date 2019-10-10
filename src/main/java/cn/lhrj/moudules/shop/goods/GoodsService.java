package cn.lhrj.moudules.shop.goods;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopGoods;

public class GoodsService {
    
	public static final GoodsService me = new GoodsService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("goods.list", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
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
		SqlPara sPara=Db.getSqlPara("goods.queryObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("goods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> selectL1() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("goods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}

	public void save(String string) {
		ShopGoods shopCategory=FastJson.getJson().parse(string, ShopGoods.class);
		shopCategory.save();
	}
	public void update(String string) {
		ShopGoods shopCategory=FastJson.getJson().parse(string, ShopGoods.class);
		shopCategory.update();
	}
	public void delectByIds(String string,Long uid) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			ShopGoods goods=new ShopGoods().findById(array.get(i));
			goods.setIsDelete(1L);
			goods.setIsOnSale(0L);
			goods.setUpdateUserId(uid);
			goods.setUpdateTime(new Date());
			goods.update();
		}
	}
    /**
	  * 下架
	*/	
	public Ret enSale(Integer id,Long uid) {
		ShopGoods goods=new ShopGoods().findById(id);	
        if (1 == goods.getIsOnSale()) {
        	 return Ret.fail("msg","此商品已处于上架状态！").set("code", 500);
        }
        goods.setIsOnSale(1L);
        goods.setUpdateUserId(uid);
        goods.setUpdateTime(new Date());
        goods.update();
        return Ret.ok();
	}
    /**
	  * 下架
	*/	
	public Ret unSale(Integer id,Long uid) {
		ShopGoods goods=new ShopGoods().findById(id);	
        if (0 == goods.getIsOnSale()) {
        	 return Ret.fail("msg","此商品已处于上架状态！").set("code", 500);
        }
        goods.setIsOnSale(0L);
        goods.setUpdateUserId(uid);
        goods.setUpdateTime(new Date());
        goods.update();
        return Ret.ok();
	}
    /**
     * 商品从回收站恢复
     */

    public void back(String string,Long uid) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			ShopGoods goods=new ShopGoods().findById(array.get(i));
			goods.setIsDelete(0L);
			goods.setIsOnSale(1L);
			goods.setUpdateUserId(uid);
			goods.setUpdateTime(new Date());
			goods.update();
		}
      
    }
}
