package cn.lhrj.moudules.admin.system.config;

import java.util.List;

import cn.lhrj.common.model.SysConfig;

public class ConfigService {

	public static final ConfigService me = new ConfigService();
	

    public List<SysConfig> getMenu() {

		 List<SysConfig> list = SysConfig.dao.find("select id,name from sys_config where type = 0 order by sort ");
		 return list;
    }
	
}
