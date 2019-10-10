package cn.lhrj.moudules.index;

import java.util.List;

import com.jfinal.plugin.ehcache.CacheKit;

import cn.lhrj.common.model.ShopOrderLog;

public class MessageService {

	public static final String messageCacheCacheName = "messageCache";
	
	public static void init() {
		CacheKit.removeAll(messageCacheCacheName);
	}
	
	public List<ShopOrderLog> getMessage() {
		String key="messagekey";
		List<ShopOrderLog> message=new ShopOrderLog().findByCache(messageCacheCacheName, key, "select id,detail as contents,create_time  from shop_order_log order by create_time desc limit 20");
		return message;
	}
	
}
