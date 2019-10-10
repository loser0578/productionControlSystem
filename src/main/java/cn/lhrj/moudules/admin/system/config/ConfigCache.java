package cn.lhrj.moudules.admin.system.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;

import cn.lhrj.common.model.SysConfig;
import cn.lhrj.common.utils.NumberUtil;

public class ConfigCache {

	private final static Log log = Log.getLog(ConfigCache.class);
	private final static SysConfig dao = new SysConfig().dao();
	private final static String allAccountsCacheName = "allconfig";
	private ConfigCache() {
	}

	public static void init() {

		log.info("####参数配置Cache初始化......");
		Map<String, SysConfig> cacheMap = new HashMap<String, SysConfig>();
		List<SysConfig> userList = dao.find("select * from sys_config  order by id ");
		for (SysConfig config : userList) {
			cacheMap.put(config.getKey(), config);
		}
		CacheKit.put(allAccountsCacheName, "cacheMap", cacheMap);
		
	}
	public static void update() {
		init();
	}

	public static SysConfig getSysConfig(String key) {
		return getSysConfigMap().get(key);
	}

	public static String getValue(String key) {
		return getSysConfig(key) == null ? null : getSysConfig(key).getValue();
	}

	public static int getValueToInt(String key) {
		return NumberUtil.parseInt(getValue(key));
	}

	public static Boolean getValueToBoolean(String key) {
		String val = getValue(key);
		try {
			return Boolean.valueOf(val);
		} catch (Exception e) {
			return false;
		}
	}

	public static String getCode(String key) {
		return getSysConfig(key) == null ? null : getSysConfig(key).getCode();
	}

	private static Map<String, SysConfig> getSysConfigMap() {
		return CacheKit.get(allAccountsCacheName,"cacheMap");
	}

}
