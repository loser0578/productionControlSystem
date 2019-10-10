package cn.lhrj.moudules.shop.ordergoods;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.ShopOrderReciver;
import cn.lhrj.common.model.ShopShipping;
import cn.lhrj.moudules.shop.order.KdniaoSubscribeAPI;

public class OrderGoodsService {
    
	public static final OrderGoodsService me = new OrderGoodsService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("order.queryList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("ordergoods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	public List<Record> queryAllReciver(Kv kv) {
		SqlPara sPara=Db.getSqlPara("ordergoods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("order.queryObject", menuid);
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
	//查询快递轨迹
	public Ret track(String orderid) throws Exception {
		ShopOrderReciver order=new ShopOrderReciver().findFirst("select * from shop_order_reciver where order_sn=?",orderid);
		if (0!=order.getShippingStatus()) {
			String shipcode=ShopShipping.getCodeByid(order.getShippingId());
			String shipnum=order.getShippingNo();
			String track=new KdniaoSubscribeAPI().getOrderTracesByJson(shipcode, shipnum);
			JSONObject jsonObject=JSONObject.parseObject(track);		
			return Ret.ok("data",jsonObject).set("code",1);
		}
	return Ret.fail("msg", "暂无物流信息").set("code", -1);
	}
	//查询快递轨迹
	public Ret trackAdmin(Integer orderid) throws Exception {
		ShopOrderReciver order=new ShopOrderReciver().findById(orderid);
		if (0!=order.getShippingStatus()) {
			String shipcode=ShopShipping.getCodeByid(order.getShippingId());
			String shipnum=order.getShippingNo();
			String track=new KdniaoSubscribeAPI().getOrderTracesByJson(shipcode, shipnum);

			JSONObject jsonObject=JSONObject.parseObject(track);
	
			
			return Ret.ok("data",jsonObject);
		}
	return Ret.fail("msg", "无法查询此订单");
	}
}
