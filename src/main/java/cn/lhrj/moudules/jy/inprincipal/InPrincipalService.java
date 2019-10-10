package cn.lhrj.moudules.jy.inprincipal;

import java.util.Date;

import com.jfinal.json.FastJson;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyPrincipal;
import cn.lhrj.common.utils.SQLUtils;

public class InPrincipalService {
	public static final InPrincipalService me = new InPrincipalService();
	
	public Page<JyPrincipal> getPage(int page,int limit,int plantId) {
		SQLUtils sql = new SQLUtils("from jy_principal where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		Page<JyPrincipal> principalPageList = new JyPrincipal().paginate(page, limit, "select * ",sql.toString());
		return principalPageList;
	}
	
	public void save(String string,int plantId) {
		JyPrincipal principal=FastJson.getJson().parse(string, JyPrincipal.class);
		Date date = new Date();
		principal.setCreateTime(date);
		principal.setUpdateTime(date);
		principal.setPlantId(plantId);
		principal.setState(1);
		principal.save();
	}
	
	public void update(String string,int plantId) {
		JyPrincipal principal=FastJson.getJson().parse(string, JyPrincipal.class);
		Date date = new Date();
		principal.setUpdateTime(date);
		principal.setPlantId(plantId);
		principal.setState(1);
		principal.update();
	}
}
