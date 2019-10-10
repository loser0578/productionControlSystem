package cn.lhrj.moudules.jy.process;

import java.util.Date;

import com.jfinal.json.FastJson;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.utils.SQLUtils;

public class ProcessService {
	
	public static final ProcessService me = new ProcessService();
	
	public Page<JyProcess> getPage(int page,int limit,int plantId) {
		SQLUtils sql = new SQLUtils("from jy_process where state = 1");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		Page<JyProcess> processPageList = new JyProcess().paginate(page, limit, "select * ",sql.toString());
		return processPageList;
	}
	
	public void save(String string,int plantId) {
		JyProcess process=FastJson.getJson().parse(string, JyProcess.class);
		Date date = new Date();
		process.setCreateTime(date);
		process.setUpdateTime(date);
		process.setState(1);
		process.setPlantId(plantId);
		process.save();
	}
	
	public void update(String string) {
		JyProcess process=FastJson.getJson().parse(string, JyProcess.class);
		Date date = new Date();
		process.setUpdateTime(date);
		process.setState(1);
		process.update();
	}
}
