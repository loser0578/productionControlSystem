package cn.lhrj.moudules.jy.parts;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class PartsController extends BaseProjectController{
	
	PartsService ps = PartsService.me;
	
	public void index() {
		render("/page/jy/parts.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyParts> records=ps.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void partsListByProductId() {
		List<JyParts> record = ps.getPartsByProductId(getParaToInt("id"));
		renderJson(Ret.ok("parts", record));
	}
	
	public void delect() {
		int partsId = getParaToInt("id");
		JyParts parts = new JyParts().findFirst("select * from jy_parts where parts_id = ?",partsId);
		parts.setState(0);
		boolean result = parts.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void update() {
		ps.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
	
	public void save() {
		int plantId = getLoginAccount().getPlantId();
		ps.save(HttpKit.readData(getRequest()),plantId);
		renderJson(Ret.ok());
	}
	
	public void allList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_parts where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyParts> partsList = new JyParts().find(sql.toString());
		renderJson(Ret.ok("partsList", partsList));
	}
}
