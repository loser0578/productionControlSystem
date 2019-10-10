package cn.lhrj.moudules.main;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.wxaapp.api.WxaAccessTokenApi;

import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.DateUtils;

public class MainService {
	
	public static final String IndexCacheCacheName = "IndexCache";
	
	public void init() {
		CacheKit.removeAll(IndexCacheCacheName);
	}
	
	
	
	public JSONArray getVisitDate() {
		String key="VisitDate";	
		JSONArray array=CacheKit.get(IndexCacheCacheName, key);
		if (array!=null) {
			return array;
		}
		JSONArray visitarray=new JSONArray();
		String baseUrl= "https://api.weixin.qq.com/datacube/getweanalysisappiddailysummarytrend?access_token=";
		JSONObject object=new JSONObject();
		for (int i = 11; i > 0; i--) {
			String url = baseUrl + WxaAccessTokenApi.getAccessToken().getAccessToken();
			
			object.put("end_date", DateUtils.yesterday(i));
			object.put("begin_date", DateUtils.yesterday(i));
			String string=HttpKit.post(url, object.toJSONString());
			System.out.println("string"+string);
			JSONObject yestday=JSONObject.parseObject(string);
			JSONArray vistinum=yestday.getJSONArray("list");
			int vistinum2=0;
			if (!vistinum.isEmpty()) {
				vistinum2=vistinum.getJSONObject(0).getIntValue("visit_total");
			}
			visitarray.add(vistinum2);
			
		}
		CacheKit.put(IndexCacheCacheName, key, visitarray);
		return visitarray;	
	}
	
	public JSONArray getUserDate() {
		String key="UserDate";
		JSONArray array=CacheKit.get(IndexCacheCacheName, key);
		if (array!=null) {
			return array;
		}
		JSONArray visitarray=new JSONArray();
		for (int i = 11; i > 0; i--) {
			DateUtils.yesterday(i);
			List<SysUser> users=new SysUser().find("select id from shop_user where  register_time <= date_sub(curdate(),interval ? day)",i);
			int vistinum2=users.size();
			
			visitarray.add(vistinum2);
		}
		CacheKit.put(IndexCacheCacheName, key, visitarray);
		return visitarray;
	}
	public JSONArray getSellDate() {
		String key="SellDate";
		JSONArray array=CacheKit.get(IndexCacheCacheName, key);
		if (array!=null) {
			return array;
		}
		JSONArray sellarray=new JSONArray();
		for (int i = 11; i > 0; i--) {
			String date=DateUtils.getdate(i);
			List<ShopOrder> orders=new ShopOrder().find("select id,actual_price,sum(actual_price) as count from shop_order where  ( datediff ( add_time , ? ) = 0 )",date);

			int vistinum2=orders.get(0).get("count") == null ? 0 : orders.get(0).getInt("count");
			sellarray.add(vistinum2);
		}
		CacheKit.put(IndexCacheCacheName, key, sellarray);
		return sellarray;
	}
	public JSONArray getGoodsDate() {
		String key="GoodsDate";
		JSONArray array=CacheKit.get(IndexCacheCacheName, key);
		if (array!=null) {
			return array;
		}
		JSONArray goodsarray=new JSONArray();
		for (int i = 11; i > 0; i--) {
			DateUtils.yesterday(i);
			List<SysUser> users=new SysUser().find("select id from shop_goods where  add_time <= date_sub(curdate(),interval ? day)",i);
			int vistinum2=users.size();
			
			goodsarray.add(vistinum2);
		}
		CacheKit.put(IndexCacheCacheName, key, goodsarray);
		return goodsarray;
	}
	
	public JSONArray getMonthSellsDate() {
		List<ShopOrder> orders=new ShopOrder().find("select id,date_format(add_time,'%Y-%m-%d') as addtime,actual_price,sum(actual_price) as count from shop_order where date_format(add_time,'%Y-%m')=date_format(now(),'%Y-%m') GROUP BY DATE_FORMAT(add_time,'%Y-%m-%d') ;");
		JSONArray goodsarray=new JSONArray();
		List<String> timelist=DateUtils.getDayListOfMonth();
		boolean b=false;
		for (int i = 0; i < timelist.size(); i++) {
			String time=timelist.get(i);
			for (int j = 0; j < orders.size(); j++) {
				if (StrKit.equals(time, orders.get(j).getStr("addtime"))) {

					goodsarray.add(i,orders.get(j).getInt("count"));
					b=true;
				}
			}

			if (!b) {
				goodsarray.add(i,0);
				b=false;
			}

			b=false;
		}
		return goodsarray;
		
	}
}
